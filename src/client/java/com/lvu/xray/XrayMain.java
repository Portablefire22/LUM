package com.lvu.xray;

import com.lvu.MainClient;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.lvu.Main.LOGGER;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;


public class XrayMain {
    final static Properties XRayBlocks = new Properties();
    static Map<String, int[]> BlockMap = new HashMap<>();


    public static void SaveProperties() {
        if (!SaveBlockMap()) {LOGGER.info("[LVU] " + "Failed saving to Block.properties!");}
    }

    public static boolean SaveBlockMap() {
        if (BlockMap.isEmpty()){
            XRayBlocks.clear();
        } else {
            for (Map.Entry<String, int[]> entry : BlockMap.entrySet()) {
                int[] arr = entry.getValue();
                String formatted = String.format("%s,%s,%s", arr[0], arr[1], arr[2]);
                XRayBlocks.put(entry.getKey(), formatted);
            }
        }
        LOGGER.info("[LVU] " + "Saving to Block.properties");
        try {
            XRayBlocks.store(new FileOutputStream("config/lvu/Block.properties"), null);
            LOGGER.info("[LVU] " + "Successfully saved to Block.properties!");
            return true;
        } catch (IOException ignored) { return false;}

    }
    public static boolean LoadBlockMap() {
        boolean i = false;
        if (!new File("config/lvu/Block.properties").exists()) {
            try{
                XRayBlocks.load(MainClient.class.getResourceAsStream("/DefaultBlock.properties"));
                i = true;
            } catch (Exception e) { return false; }
        } else {
            try {
                XRayBlocks.load(new FileInputStream("config/lvu/Block.properties"));
            }catch(IOException ignored) {return false;}}
        LOGGER.info("Properties Loaded!");
        for (String key : XRayBlocks.stringPropertyNames()) {
            String[] values = XRayBlocks.get(key).toString().split(",");
            int[] Colours = new int[]{Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2])};
            BlockMap.put(key, Colours);
        }
        if (i) {
            SaveProperties();
        }
        return true;
    }

    private static boolean ResetBlockMap(CommandContext<FabricClientCommandSource> context) {
        try {
            XRayBlocks.clear();
            BlockMap.clear();
            XRayBlocks.load(MainClient.class.getResourceAsStream("/DefaultBlock.properties"));
            for (String key : XRayBlocks.stringPropertyNames()) {
                String[] values = XRayBlocks.get(key).toString().split(",");
                int[] Colours = new int[]{Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2])};
                BlockMap.put(key, Colours);
            }
            LOGGER.info("[LVU] " + "Reset BlockMap!");
            SaveProperties();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean LoadProperties() {
        LOGGER.info("[LVU] " + "Loading Properties!");
        if (!LoadBlockMap()) {LOGGER.info("[LVU] " + "Failed loading Block.properties!");}
        return true;
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
            red = 900;
            green = 0;
            blue = 255;
        }

        return new int[]{red, green, blue};
    }

    public static int WhitelistRemove(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        String arg = getString(context, "Block Name");
        String formatted = null;
        if(arg != null) {
            if (!arg.contains("block.") & !arg.equals("all")) {
                arg = "block.minecraft." + arg;
            }
            //LOGGER.info(arg + " == " + "all" + " | " + String.valueOf(!arg.trim().equals("all")));
            if (!arg.trim().equals("all")) {
                if (BlockMap.containsKey(arg)) {
                    BlockMap.remove(arg);
                    formatted = "Removed: " + arg;
                } else {
                    throw new SimpleCommandExceptionType(Text.translatable("block.does.not.exist.in.config")).create();
                }
            } else {
                BlockMap.clear();
                formatted = "Removed all blocks!";
            }
            SaveBlockMap();
            context.getSource().sendFeedback(Text.literal(formatted));
            return Command.SINGLE_SUCCESS;
        } else {
            throw new SimpleCommandExceptionType(Text.translatable("arguments.missing")).create();
        }

    }

    public static int Whitelist(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        String arg = getString(context, "Block Name");
        String dec = getString(context, "Decision");
        Integer col1 = null;
        Integer col2 = null;
        Integer col3 = null;
        try {
            col1 = IntegerArgumentType.getInteger(context, "Red");
            col2 = IntegerArgumentType.getInteger(context, "Green");
            col3 = IntegerArgumentType.getInteger(context, "Blue");
        } catch (Exception ignored) {}
        if (arg != null && dec != null) {
            if (!arg.contains("block.")) {
                arg = "block.minecraft." + arg;
            }
            String formatted = null;
            int[] Colours = new int[]{col1,col2,col3};
            switch (dec){
                case "add":
                    BlockMap.put(arg, Colours);
                    formatted = "Added: " + arg + " Colours: " + "<" + col1 + "," + col2 + "," + col3 + ">";
                    break;
                case "modify":
                    if(BlockMap.containsKey(arg)){
                        BlockMap.put(arg, Colours);
                        formatted = "Modified to: " + arg + " Colours: " + "<" + col1 + "," + col2 + "," + col3 + ">";
                    }
                    break;
                default:
                    formatted = "Command could not determine the decision!";
            }
            SaveBlockMap();
            if(LoadProperties()) {
                context.getSource().sendFeedback(Text.literal(formatted));
                return Command.SINGLE_SUCCESS;
            } else {
                return 0;
            }
        } else {
            throw new SimpleCommandExceptionType(Text.translatable("arguments.missing")).create();
        }
    }
    public static int Reset(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(Text.literal("Resetting X-Ray config to default settings!"));
        ResetBlockMap(context);
        return 1;
    }
}
