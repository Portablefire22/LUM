package com.lvu;

import net.minecraft.client.MinecraftClient;


public class SaveMap implements net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.ClientStopping {

    @Override
    public void onClientStopping(MinecraftClient client) {
        ExampleModClient.SaveProperties();
    }
}
