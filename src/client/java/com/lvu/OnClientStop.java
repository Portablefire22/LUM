package com.lvu;

import com.lvu.xray.chunk.XrayChunkManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;

public class OnClientStop implements ClientLifecycleEvents.ClientStopping{
    @Override
    public void onClientStopping(MinecraftClient client) {
        try {
            XrayChunkManager.SaveChunksToFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
