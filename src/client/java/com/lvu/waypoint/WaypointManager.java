package com.lvu.waypoint;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;

public class WaypointManager {

    public static ArrayList<Waypoint> Waypoints;
    public void WaypointCommand(CommandContext<FabricClientCommandSource> context) {

    }

    public static boolean CreateWaypoint(Waypoint waypoint) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        MatrixStack matrix = new MatrixStack();
        // Get the coordinates it should be at
        matrix.translate(waypoint.getX(), waypoint.getY(), waypoint.getZ());
        // Make it relative to the player
        assert player != null;
        matrix.translate(-player.getX(), -player.getY(), -player.getZ());
        VertexConsumerProvider vertexConsumer = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        BeaconBlockEntityRenderer.renderBeam(matrix, vertexConsumer,BeaconBlockEntityRenderer.BEAM_TEXTURE, 5 ,5 ,5 ,5, 5, new float[]{waypoint.getRed(), waypoint.getGreen(), waypoint.getBlue()} , 5, 5  );
        return true;
    }

    public void RenderWaypoint() {

    }

    public static int CreateWaypoint(CommandContext<FabricClientCommandSource> context) {
        Waypoint waypoint = new Waypoint(context.getSource().getPlayer(), StringArgumentType.getString(context, "Waypoint Name"));
        if (Waypoints == null) {
            Waypoints = new ArrayList<Waypoint>();
        }
        Waypoints.add(waypoint);
        return Command.SINGLE_SUCCESS;
    }
}
