package com.lvu.xray;

import com.lvu.Main;
import com.lvu.xray.render.BlockHighlightListener;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

import java.util.Set;

import static com.lvu.xray.render.BlockHighlightListener.range;

public class LVUChunkManager implements ClientChunkEvents.Load, ClientChunkEvents.Unload {
    @Override
    public void onChunkLoad(ClientWorld world, WorldChunk chunk) {
        Set<ChunkPos> chunkSet = BlockHighlightListener.chunks;
        if(chunkSet != null) {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;
            //System.out.println(player);
            if (player != null) {
                int cX = player.getChunkPos().x;
                int cZ = player.getChunkPos().z;
                // If Loaded chunk is inline with player X then no Changes
                int dX = chunk.getPos().x - cX;
                int dZ = chunk.getPos().z - cZ;
                // If distance is greater than the range, dont continue

                if (Math.abs(dX) > range || Math.abs(dZ) > range) {
                    return;
                }
                boolean isX = (chunk.getPos().x - cX) == 0;
                // If Loaded chunk is inline with player X then no Changes
                boolean isZ = (chunk.getPos().z - cZ) == 0;
                Main.LOGGER.info("dX: " + String.valueOf(dX));
                Main.LOGGER.info("dZ " + String.valueOf(dZ));
                if (isX){
                    chunkSet.add(new ChunkPos(cX, dZ));
                } else if (isZ) {
                    chunkSet.add(new ChunkPos(dX, dX));
                }

            }

        }
    }

    @Override
    public void onChunkUnload(ClientWorld world, WorldChunk chunk) {
        Set<ChunkPos> chunkSet = BlockHighlightListener.chunks;
        if(chunkSet != null) {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;
            //System.out.println(player);
            if (player != null) {
                int cX = player.getChunkPos().x;
                int cZ = player.getChunkPos().z;
                // If Loaded chunk is inline with player X then no Changes
                int dX = chunk.getPos().x - cX;
                int dZ = chunk.getPos().z - cZ;
                // If distance is greater than the range, dont continue
                if (Math.abs(dX) > range || Math.abs(dZ) > range) {
                    return;
                }
                boolean isX = (chunk.getPos().x - cX) == 0;
                // If Loaded chunk is inline with player X then no Changes
                boolean isZ = (chunk.getPos().z - cZ) == 0;

                if (isX){
                    chunkSet.remove(new ChunkPos(cX, dZ));
                } else if (isZ) {
                    chunkSet.remove(new ChunkPos(dX, dX));
                }

            }
        }
    }
}
