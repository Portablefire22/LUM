package com.lvu;

import com.lvu.waypoint.WaypointManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

import java.io.IOException;

public class WorldDisconnect implements ClientPlayConnectionEvents.Disconnect {

    @Override
    public void onPlayDisconnect(ClientPlayNetworkHandler handler, MinecraftClient client) {
        try {
            WaypointManager.SaveWaypoints();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
