package com.lvu;

import com.lvu.waypoint.WaypointManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

import java.io.IOException;

public class WorldJoin implements ClientPlayConnectionEvents.Join{

    @Override
    public void onPlayReady(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        //WaypointManager.Waypoints.clear();
        try {
            WaypointManager.LoadWaypoints();
        } catch (IOException ignored) {
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}

