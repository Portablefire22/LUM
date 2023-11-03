package com.lvu.xray;

import com.lvu.Main;
import com.lvu.MainClient;
import com.lvu.xray.chunk.XrayChunkManager;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class BlockManager {

    public static ArrayList<int[]> GetBlocks(WorldRenderContext context, Set<ChunkPos> chunks) {
        ArrayList<int[]> BlockCoord = new ArrayList<>();
        for (ChunkPos chunkPos : chunks) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = -63; y < 255; y++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        BlockState block = context.world().getChunk(chunkPos.getStartPos()).getBlockState(pos);
                        if (Xray.shouldBlockBeRendered(block)) {
                            // Working with relative coords, so we need to turn this into a world coordinate from the chunk
                            int offsetX = x + chunkPos.getStartX();
                            int offsetZ = z + chunkPos.getStartZ();

                            // Anti-cheats often place fake blocks in non-visible areas.
                            // This ensures the block is visible before rendering it to avoid the anti-cheat.
                            // This displays fewer ores but negates the anti-cheat.
                            if (MainClient.UtilityStatus.get("xray.legit").toString().equals("true")) {
                                if (isVisible(context.world(), offsetX, y, offsetZ)) { continue; }
                            }
                            BlockCoord.add(new int[]{offsetX, y, offsetZ});
                        }
                    }
                }
            }
        }
        return BlockCoord;
    }

    public static ArrayList<int[]> GetBlocksHashSearch( WorldRenderContext context, Set<ChunkPos> chunks){
        ArrayList<int[]> BlockCoord = new ArrayList<>();
        for (ChunkPos chunkPos : chunks) {
            for (String BlockName : Xray.BlockMap.keySet()){
                ArrayList<int[]> Blocks = null;
                boolean i = true;
                int loop = 0;
                while (i) {
                    if (XrayChunkManager.ChunkMap.get(chunkPos) != null && !XrayChunkManager.ChunkMap.get(chunkPos).isEmpty()) {
                        loop = 0;
                        Blocks = XrayChunkManager.ChunkMap.get(chunkPos).get(BlockName);
                        i = false;
                    } else {
                        if(loop>5) {
                            i = false;
                        }
                        /*
                            TODO
                            FIX THE LOOP STICKING WHEN A CHUNK IS NOT LOADED
                            AND FIX THIS LOOP STICKING WHEN LOADING INTO THE GAME
                         */
                        if (!context.world().isChunkLoaded(chunkPos.getStartX(), chunkPos.getStartZ())) {
                            Main.LOGGER.info("Relative not loaded, skipping!: " + chunkPos.getRegionRelativeX() + " " + chunkPos.getRegionRelativeZ());
                            i = false;
                            continue;
                        } else {
                            XrayChunkManager.ChunkMap.put(chunkPos, BlocksToHash(context.world(), context.world().getChunk(chunkPos.getRegionRelativeX(), chunkPos.getRegionRelativeZ())));
                            Main.LOGGER.info("Relative: " + chunkPos.getRegionRelativeX() + " " + chunkPos.getRegionRelativeZ());
                            Main.LOGGER.info("Region: " + chunkPos.getRegionX() + " " + chunkPos.getRegionZ());
                            Main.LOGGER.info(String.valueOf(context.world().isChunkLoaded(chunkPos.getRegionRelativeX(), chunkPos.getRegionRelativeZ())));
                        }
                    }
                    if (Blocks != null) {
                        for (int[] Block : Blocks) {
                            if (!Blocks.isEmpty()) {
                                if (Block.length == 4 && MainClient.UtilityStatus.get("xray.legit").toString().equals("true")) {
                                    if (Block[3] == 1) {
                                        BlockCoord.add(new int[]{Block[0], Block[1], Block[2]});
                                    }
                                } else {
                                    BlockCoord.add(new int[]{Block[0], Block[1], Block[2]});
                                }
                            }
                        }
                    }
                    loop++;
                }
            }
        }
        return BlockCoord;
    }

    public static boolean isVisible(ClientWorld world, int x, int y, int z) {

        /* TODO
            Find a better solution to detecting if the block should be visible.
        */
        int blocks = 0;
        String b0 = world.getBlockState(new BlockPos(x - 1, y, z)).getBlock().getTranslationKey();
        String b1 = world.getBlockState(new BlockPos(x + 1, y, z)).getBlock().getTranslationKey();
        String b2 = world.getBlockState(new BlockPos(x, y + 1, z)).getBlock().getTranslationKey();
        String b3 = world.getBlockState(new BlockPos(x, y - 1, z)).getBlock().getTranslationKey();
        String b4 = world.getBlockState(new BlockPos(x, y, z + 1)).getBlock().getTranslationKey();
        String b5 = world.getBlockState(new BlockPos(x, y, z - 1)).getBlock().getTranslationKey();
        if (b0.contains("air") || b0.contains("water") || b0.contains("lava")) {blocks++;}
        else if (b1.contains("air") || b1.contains("water") || b1.contains("lava")) {blocks++;}
        else if (b2.contains("air") || b2.contains("water") || b2.contains("lava")) {blocks++;}
        else if (b3.contains("air") || b3.contains("water") || b3.contains("lava")) {blocks++;}
        else if (b4.contains("air") || b4.contains("water") || b4.contains("lava")) {blocks++;}
        else if (b5.contains("air") || b5.contains("water") || b5.contains("lava")) {blocks++;}
        return blocks > 0;
    }

    // Converts blocks in a chunk to a hashmap.
    public static HashMap<String, ArrayList<int[]>> BlocksToHash(ClientWorld world, WorldChunk chunk) {
        HashMap<String, ArrayList<int[]>> ChunkMap = new HashMap<>();
        ChunkPos chunkPos = chunk.getPos();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = -63; y < 255; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState block = chunk.getBlockState(pos);
                    String blockName = block.getBlock().getTranslationKey();
                    if (blockName.contains("air")) { continue; }
                    // Working with relative coords, so we need to turn this into a world coordinate from the chunk
                    int offsetX = x + chunkPos.getStartX();
                    int offsetZ = z + chunkPos.getStartZ();

                    // Anti-cheats often place fake blocks in non-visible areas.
                    // This ensures the block is visible before rendering it to avoid the anti-cheat.
                    // This displays fewer ores but negates the anti-cheat.
                    int i = 0;
                    if (MainClient.UtilityStatus.get("xray.legit").toString().equals("true")) {
                        if (isVisible(world, offsetX, y, offsetZ)) { i = 1;; }
                    }

                    ArrayList<int[]> blocks = new ArrayList<>();
                    if (ChunkMap.containsKey(blockName)) {
                        blocks = ChunkMap.get(blockName);
                        blocks.add(new int[]{offsetX, y, offsetZ, i});
                    } else {
                        blocks.add(new int[]{offsetX, y, offsetZ, i});
                    }
                    ChunkMap.put(blockName,blocks);
                }
            }
        }
        return ChunkMap;
    }
}




