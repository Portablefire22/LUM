package com.lvu.xray.chunk;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.chunk.WorldChunk;

public class OnChunkLoad implements  ClientChunkEvents.Load{
    MinecraftClient client = MinecraftClient.getInstance();


    @Override
    public void onChunkLoad(ClientWorld world, WorldChunk chunk) {
        ClientPlayerEntity player = client.player;
        if(player != null) {
            //XrayChunkManager.ChunkToHash(world, chunk);
        }
    }
}
