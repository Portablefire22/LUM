package com.lvu;

import com.lvu.xray.XrayMain;
import com.lvu.xray.render.BlockHighlightListener;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.command.CommandSource;
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
		WorldRenderEvents.END.register(new BlockHighlightListener());
		//ClientChunkEvents.CHUNK_LOAD.register(new LVUChunkManager());
		//ClientLifecycleEvents.CLIENT_STOPPING.register(SaveMap());

		//ClientChunkEvents.CHUNK_UNLOAD.register(new Unload());
	}
	//public ClientLifecycleEvents.ClientStopping SaveMap() {
	//	SaveUtilityProperties();
	//	XrayMain.SaveProperties();
	//	return null;
	//}
	public static boolean LoadUtilityProperties() {
		if (!(new File("config/lvu/Utilities.properties").exists())) {
			try{
				LOGGER.info("[LVU] No 'Utilities.properties' found, using default settings!");
				UtilityStatus.load(MainClient.class.getResourceAsStream("/DefaultUtilities.properties"));
				LOGGER.info("[LVU] Saving default settings to config.");
				SaveUtilityProperties();
				LOGGER.info("[LVU] Saved default settings.");
			} catch (Exception e) {
				LOGGER.error("[LVU] Error loading properties & saving to file!");
				return false;
			}
		} else {
			try {
				UtilityStatus.load(new FileInputStream("config/lvu/Utilities.properties"));
			}catch(IOException ignored) {return false;}}
		LOGGER.info("Properties Loaded!");
		return true;
	}

	public static boolean SaveUtilityProperties() {
		try {
			UtilityStatus.store(new FileOutputStream("config/lvu/Utilities.properties"), null);
			LOGGER.info("[LVU] " + "Successfully saved to Utilities.properties!");
			return true;
		} catch (IOException ignored) { return false;}
	}

	// Load all property files when client is initialised
	public static boolean LoadProperties() {
		try{
			XrayMain.LoadProperties();
			LoadUtilityProperties();
			return true;
		} catch(Exception e) {
			return false;
		}
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

	private static SuggestionProvider<FabricClientCommandSource> XRAY_OPTIONS =
		(ctx, builder) -> CommandSource.suggestMatching("add\nmodify".lines(), builder);

	private static SuggestionProvider<FabricClientCommandSource> MOD_OPTIONS =
			(ctx, builder) -> CommandSource.suggestMatching("xray".lines(), builder);

	private static SuggestionProvider<FabricClientCommandSource> ENABLE_OR_DISABLE =
			(ctx, builder) -> CommandSource.suggestMatching("enable\ndisable".lines(), builder);


	public void RegisterCommands() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, environment) -> dispatcher.register(literal("refresh").executes(MainClient::RefreshProperties)));
		long i = 2048532523523523L;
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, environment) -> {
			final LiteralCommandNode<FabricClientCommandSource> removeNode =
					dispatcher.register(
							literal("xray")
									.then(literal("remove")
											.then(argument("Block Name", StringArgumentType.string())
													.executes(XrayMain::WhitelistRemove))));

			final LiteralCommandNode<FabricClientCommandSource> blockNode =
				dispatcher.register(
						literal("xray")
								.then(literal("block")
										.then(argument("Decision", StringArgumentType.string()).suggests(XRAY_OPTIONS)
												.then(argument("Block Name", StringArgumentType.string())
														.then(argument("Red", IntegerArgumentType.integer())
																.then(argument("Green", IntegerArgumentType.integer())
																		.then(argument("Blue", IntegerArgumentType.integer())
														.executes(XrayMain::Whitelist))))))));
			final LiteralCommandNode<FabricClientCommandSource> toggleMod =
					dispatcher.register(
							literal("lvu")
									.then(literal("utility")
											.then(argument("Utility Name", StringArgumentType.string()).suggests(MOD_OPTIONS)
													.then(argument("Decision", StringArgumentType.string()).suggests(ENABLE_OR_DISABLE)
															.executes(this::UtilityActivation)))));
			final LiteralCommandNode<FabricClientCommandSource> changeSetting =
					dispatcher.register(
							literal("lvu")
									.then(literal("setting")
											.then(argument("Utility Name", StringArgumentType.string()).suggests(MOD_OPTIONS)
													.then(argument("Setting", StringArgumentType.string()).suggests(ENABLE_OR_DISABLE)
														.then(argument("Value", StringArgumentType.string()).suggests(ENABLE_OR_DISABLE)
																.executes(this::UtilitySetting))))));
		});
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, environment) ->
				dispatcher.register(
						literal("xray")
								.then(literal("reset").executes(XrayMain::Reset))));
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
		SaveUtilityProperties();
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
							if (Value.equals("enable") || Value.equals("disable") || Value.equals("true") || Value.equals("false")) {
								boolean ValueBool = Value.equals("enable") || Value.equals("true");
								UtilityStatus.setProperty(Utility+"."+Setting, String.valueOf(ValueBool));
								context.getSource().sendFeedback(Text.of("Legit X-Ray Enabled!"));
							} else {
								UtilityStatus.setProperty(Utility+"."+Setting, String.valueOf(false));
								context.getSource().sendFeedback(Text.of("Legit X-Ray Disabled!"));
							}
						break;
					case "range":
						UtilityStatus.setProperty(Utility+"."+Setting, String.valueOf(Value));
						break;
					case "pause":
						if (Value.equals("enable") || Value.equals("disable") || Value.equals("true") || Value.equals("false")) {
							boolean ValueBool = Value.equals("enable") || Value.equals("true");
							UtilityStatus.setProperty(Utility+"."+Setting, String.valueOf(ValueBool));
							context.getSource().sendFeedback(Text.of("X-Ray Paused!"));
						} else {
							UtilityStatus.setProperty(Utility+"."+Setting, String.valueOf(false));
							context.getSource().sendFeedback(Text.of("X-Ray Unpaused!"));
						}
						break;
					default:
						throw new SimpleCommandExceptionType(Text.translatable("setting.missing")).create();
				}
				break;
			default:
				throw new SimpleCommandExceptionType(Text.translatable("utility.missing")).create();
		}
		SaveUtilityProperties();
		return Command.SINGLE_SUCCESS;
	}
}