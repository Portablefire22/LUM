package com.lvu;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.chunk.WorldChunk;

public class Unload implements net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.Unload{
    @Override
    public void onChunkUnload(ClientWorld world, WorldChunk chunk) {
        //ExampleModClient.SaveProperties();
    }
}
