package com.lvu;

import com.lvu.xray.Xray;
import com.lvu.xray.chunk.OnChunkLoad;
import com.lvu.xray.render.Render;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.text.Text;

import static com.lvu.Main.LOGGER;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class MainClient implements ClientModInitializer {

	public static Properties UtilityStatus = new Properties();
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		// Create directory
		new File("config/lvu").mkdirs();
		LoadProperties();
		RegisterCommands();
		WorldRenderEvents.END.register(new Render());
		ClientChunkEvents.CHUNK_LOAD.register(new OnChunkLoad());
		ClientLifecycleEvents.CLIENT_STOPPING.register(new OnClientStop());
	}
	public static boolean LoadUtilityProperties() {
		if (!(new File("config/lvu/Utilities.properties").exists())) {
			try{
				LOGGER.info("[LVU] No 'Utilities.properties' found, using default settings!");
				UtilityStatus.load(MainClient.class.getResourceAsStream("/DefaultUtilities.properties"));
				LOGGER.info("[LVU] Saving default settings to config.");
				SaveProperties(UtilityStatus, "config/lvu/Utilities.properties");
				LOGGER.info("[LVU] Saved default settings.");
			} catch (Exception e) {
				LOGGER.error("[LVU] Error loading properties & saving to file!");
				return false;
			}
		} else {
			try {
				UtilityStatus.load(new FileInputStream("config/lvu/Utilities.properties"));
			}catch(IOException ignored) {return false;}}
		LOGGER.info("Utility Properties Loaded!");
		return true;
	}

	public static boolean SaveProperties(Properties Prop, String Path) {
		try {
			Prop.store(new FileOutputStream(Path), null);
			LOGGER.info("[LVU] " + "Successfully saved to " + Path + "!");
			return true;
		} catch (IOException ignored) { return false;}
	}

	// Load all property files when client is initialised
	public static boolean LoadProperties() {
		try{
			Xray.LoadProperties();
			LoadUtilityProperties();
			// Currently only the main settings require this.
			LoadDefaults("/DefaultUtilities.properties", UtilityStatus);
			return true;
		} catch(Exception e) {
			return false;
		}
	}

	// Iterate through settings and add missing settings, allows for old configs to be used after updates.
	public static boolean LoadDefaults(String DefaultPath, Properties User) {
		Properties Default  = new Properties();
		boolean Added = false;
		try {
			Default.load(MainClient.class.getResourceAsStream(DefaultPath));
		}catch(IOException ignored) {return false;}
		LOGGER.info("Default File Successfully Loaded.");
		for (String key : Default.stringPropertyNames()) {
			if (!User.containsKey(key.toString())) {
				User.put(key, Default.get(key));
				Added = !Added;
			}
		}
		SaveProperties(UtilityStatus, "config/lvu/Utilities.properties");
		if (Added) {
			LOGGER.info("Missing settings were added from: " + DefaultPath + "!");
			return true;
		}
		return false;
	}


	public static int RefreshProperties(CommandContext<FabricClientCommandSource> context) {
		context.getSource().sendFeedback(Text.literal("Refreshing Properties."));
		if (LoadProperties()){
			context.getSource().sendFeedback(Text.literal("Properties Successfully refreshed."));
		} else {
			context.getSource().sendFeedback(Text.literal("Properties Failed to refresh!"));
		}
		return 1;
	}

	public void RegisterCommands() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, environment) -> dispatcher.register(literal("refresh").executes(MainClient::RefreshProperties)));
		long i = 2048532523523523L;
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, environment) -> {
			final LiteralCommandNode<FabricClientCommandSource> removeNode =
					dispatcher.register(
							literal("xray")
									.then(literal("remove")
											.then(argument("Block Name", StringArgumentType.string())
													.executes(Xray::WhitelistRemove))));

			final LiteralCommandNode<FabricClientCommandSource> blockNode =
				dispatcher.register(
						literal("xray")
								.then(literal("block")
										.then(argument("Decision", StringArgumentType.string()).suggests(CustomSuggestionProviders.XRAY_OPTIONS)
												.then(argument("Block Name", StringArgumentType.string())
														.then(argument("Red", IntegerArgumentType.integer())
																.then(argument("Green", IntegerArgumentType.integer())
																		.then(argument("Blue", IntegerArgumentType.integer())
														.executes(Xray::Whitelist))))))));
			final LiteralCommandNode<FabricClientCommandSource> toggleMod =
					dispatcher.register(
							literal("lvu")
									.then(literal("utility")
											.then(argument("Utility Name", StringArgumentType.string()).suggests(CustomSuggestionProviders.MOD_OPTIONS)
													.then(argument("Decision", StringArgumentType.string()).suggests(CustomSuggestionProviders.ENABLE_OR_DISABLE)
															.executes(this::UtilityActivation)))));
			final LiteralCommandNode<FabricClientCommandSource> changeSetting =
					dispatcher.register(
							literal("lvu")
									.then(literal("setting")
											.then(argument("Utility Name", StringArgumentType.string()).suggests(CustomSuggestionProviders.MOD_OPTIONS)
													.then(argument("Setting", StringArgumentType.string()).suggests(CustomSuggestionProviders.ENABLE_OR_DISABLE)
														.then(argument("Value", StringArgumentType.string()).suggests(CustomSuggestionProviders.ENABLE_OR_DISABLE)
																.executes(this::UtilitySetting))))));
		});
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, environment) ->
				dispatcher.register(
						literal("xray")
								.then(literal("reset").executes(Xray::Reset))));
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, environment) ->
				dispatcher.register(
						literal("xray")
								.then(literal("resetworld").executes(Xray::WorldReset))));
	}



	private int UtilityActivation(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
		String Utility = getString(context, "Utility Name");
		boolean Decision = getString(context, "Decision").equals("enable");
		switch (Utility) {
			case "xray":
					UtilityStatus.setProperty(Utility, String.valueOf(Decision));
				break;
			default:
				throw new SimpleCommandExceptionType(Text.translatable("utility.missing")).create();
		}
		SaveProperties(UtilityStatus, "config/lvu/Utilities.properties");
		return Command.SINGLE_SUCCESS;
	}

	private int UtilitySetting(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
		String Utility = getString(context, "Utility Name");
		String Setting = getString(context, "Setting");
		String Value = getString(context, "Value");
		switch (Utility) {
			case "xray":
				switch(Setting){
					case "legit":
						if (SimpleEnableCheck(Utility, Setting, Value)) {
							context.getSource().sendFeedback(Text.of("Legit Mode Enabled!"));
						} else {
							context.getSource().sendFeedback(Text.of("Legit Mode Disabled!"));
						}
						break;
					case "range":
						UtilityStatus.setProperty(Utility+"."+Setting, String.valueOf(Value));
						break;
					case "pause":
						if (SimpleEnableCheck(Utility, Setting, Value)) {
							context.getSource().sendFeedback(Text.of("X-Ray Paused!"));
						} else {
							context.getSource().sendFeedback(Text.of("X-Ray Unpaused!"));
						}
						break;
					case "experimentalsearch":
						if (SimpleEnableCheck(Utility, Setting, Value)) {
							context.getSource().sendFeedback(Text.of("Experimental Search Enabled!"));
						} else {
							context.getSource().sendFeedback(Text.of("Experimental Search Disabled!"));
						}
						break;
					default:
						throw new SimpleCommandExceptionType(Text.translatable("setting.missing")).create();
				}
				break;
			default:
				throw new SimpleCommandExceptionType(Text.translatable("utility.missing")).create();
		}
		SaveProperties(UtilityStatus, "config/lvu/Utilities.properties");
		return Command.SINGLE_SUCCESS;
	}

	private boolean SimpleEnableCheck(String Utility, String Setting, String Value) {
		if (Value.equals("enable") || Value.equals("true") ) {
			UtilityStatus.setProperty(Utility+"."+Setting, String.valueOf(true));
			return true;
		} else if (Value.equals("disable") || Value.equals("false")){
			UtilityStatus.setProperty(Utility+"."+Setting, String.valueOf(false));
		}
		return false;
	}
}