package com.lvu;

import com.lvu.xray.XrayMain;
import net.minecraft.client.MinecraftClient;


public class SaveMap implements net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.ClientStopping {

    @Override
    public void onClientStopping(MinecraftClient client) {
        XrayMain.SaveProperties();
    }
}
