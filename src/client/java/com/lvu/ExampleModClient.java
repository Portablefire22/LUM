package com.lvu;

import com.lvu.render.BlockHighlightListener;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ExampleModClient implements ClientModInitializer {
	final static Properties properties = new Properties();
	static Map<String, int[]> BlockMap = new HashMap<>();
	static Logger LOGGER = LogManager.getLogger("LVU");

	public static void SaveProperties() {
		for (Map.Entry<String, int[]> entry : BlockMap.entrySet()){
			int[] arr = entry.getValue();
			String formatted = String.format("%s,%s,%s",arr[0], arr[1], arr[2]);
			properties.put(entry.getKey(), formatted);
		}
		LOGGER.log(Level.INFO, "[LVU] " + "Saving to Block.properties");
		try {
			properties.store(new FileOutputStream("config/lvu/Block.properties"), null);
			LOGGER.log(Level.INFO, "[LVU] " + "Successfully saved to Block.properties!");
		} catch (IOException ignored) {}
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
	public static boolean LoadProperties() {
		LOGGER.log(Level.INFO, "[LVU] " + "Loading Properties!");
		boolean i = false;
		if (new File("config/lvu/").mkdirs()) {
			try {
				properties.load(ExampleModClient.class.getResourceAsStream("/DefaultBlock.properties"));
				i = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				properties.load(new FileInputStream("config/lvu/Block.properties"));
			}catch(IOException ignored) {return false;}}
		LOGGER.log(Level.INFO, "Properties Loaded!");
		for (String key : properties.stringPropertyNames()) {
			String[] values = properties.get(key).toString().split(",");
			int[] Colours = new int[]{Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2])};
			BlockMap.put(key, Colours);
		}
		if (i) {
			SaveProperties();
		}
		return true;
	}

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		WorldRenderEvents.END.register(new BlockHighlightListener());
		LoadProperties();
		ClientLifecycleEvents.CLIENT_STOPPING.register(new SaveMap());

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, environment) -> dispatcher.register(literal("refresh").executes(ExampleModClient::RefreshProperties)));
		//ClientChunkEvents.CHUNK_UNLOAD.register(new Unload());
	}

	//static Pattern pattern = Pattern.compile("(.*)((?=gold|iron|diamond|copper|coal|redstone|emerald)|(?=_ore|_debris))(.*)");
	//static Matcher matcher = pattern.matcher("");
	public static boolean shouldBlockBeRendered(BlockState state){
		String str = state.getBlock().getTranslationKey();
		/*matcher.reset(str);
		if (!BlockMap.containsKey(str)){
			//System.out.println(str);
			if (matcher.matches()){
				BlockMap.put(str, new int[]{0,0,0});
				return true;
			}
		}*/
		return BlockMap.containsKey(str);
    }

	public static int[] GetblockColour(BlockState block) {
		String str = block.getBlock().getTranslationKey();
		int[] col = BlockMap.get(str);
		int red;
		int green;
		int blue;
		if(col != null) {
			red = col[0];
			green = col[1];
			blue = col[2];
		} else {
			red = 255;
			green = 0;
			blue = 255;
		}

		return new int[]{red, green, blue};
	}
}