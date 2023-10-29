package com.lvu.xray;

import com.lvu.MainClient;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.ArrayList;
import java.util.Set;

public class BlockManager {

    public static ArrayList<int[]> GetBlocks(WorldRenderContext context) {
        Set<ChunkPos> chunks = ChunkManager.getchunks(context);
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
                            // This displays less ores but negates the anti-cheat.
                            if (MainClient.UtilityStatus.get("xray.legit").toString().equals("true")) {
                                if (isVisible(context, offsetX, y, offsetZ)) { continue; }
                            }
                            BlockCoord.add(new int[]{offsetX, y, offsetZ});
                        }
                    }
                }
            }
        }
        return BlockCoord;
    }

    public static boolean isVisible(WorldRenderContext context, int x, int y, int z) {

        /* TODO
            Find a better solution to detecting if the block should be visible.
        */
        int blocks = 0;
        if (context.world().getBlockState(new BlockPos(x + 1, y, z)).getBlock().getTranslationKey().contains("air") || context.world().getBlockState(new BlockPos(x + 1, y, z)).getBlock().getTranslationKey().contains("water") || context.world().getBlockState(new BlockPos(x + 1, y, z)).getBlock().getTranslationKey().contains("lava")) {blocks++;}
        else if (context.world().getBlockState(new BlockPos(x - 1, y, z)).getBlock().getTranslationKey().contains("air") || context.world().getBlockState(new BlockPos(x - 1, y, z)).getBlock().getTranslationKey().contains("water") || context.world().getBlockState(new BlockPos(x - 1, y, z)).getBlock().getTranslationKey().contains("lava")) {blocks++;}
        else if (context.world().getBlockState(new BlockPos(x, y + 1, z)).getBlock().getTranslationKey().contains("air") || context.world().getBlockState(new BlockPos(x, y + 1, z)).getBlock().getTranslationKey().contains("water") || context.world().getBlockState(new BlockPos(x, y + 1, z)).getBlock().getTranslationKey().contains("lava")) {blocks++;}
        else if (context.world().getBlockState(new BlockPos(x, y - 1, z)).getBlock().getTranslationKey().contains("air") || context.world().getBlockState(new BlockPos(x, y - 1, z)).getBlock().getTranslationKey().contains("water") || context.world().getBlockState(new BlockPos(x, y - 1, z)).getBlock().getTranslationKey().contains("lava")) {blocks++;}
        else if (context.world().getBlockState(new BlockPos(x, y, z + 1)).getBlock().getTranslationKey().contains("air") || context.world().getBlockState(new BlockPos(x, y, z + 1)).getBlock().getTranslationKey().contains("water") || context.world().getBlockState(new BlockPos(x, y, z + 1)).getBlock().getTranslationKey().contains("lava")) {blocks++;}
        else if (context.world().getBlockState(new BlockPos(x, y, z - 1)).getBlock().getTranslationKey().contains("air") || context.world().getBlockState(new BlockPos(x, y, z - 1)).getBlock().getTranslationKey().contains("water") || context.world().getBlockState(new BlockPos(x, y, z - 1)).getBlock().getTranslationKey().contains("lava")) {blocks++;}
        return blocks > 0;
    }
}




