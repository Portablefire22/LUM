package com.lvu.waypoint;

import com.lvu.MainClient;
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
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static com.lvu.Main.LOGGER;

public class WaypointManager {
    public static HashMap<String, Waypoint> Waypoints;
    public void WaypointCommand(CommandContext<FabricClientCommandSource> context) {

    }

    // TODO Command to display waypoint information in chat.

    public static boolean SaveWaypoints() throws IOException {
        if (Waypoints == null) return false;
        if (Waypoints.isEmpty()) return true;

        String FilePath = String.format("config/lvu/Worlds/%s/", MainClient.GetPlayerWorld());
        Files.createDirectories(Paths.get(FilePath));
        FilePath = FilePath + "Waypoints.dat";
        FileOutputStream fileOut = new FileOutputStream(FilePath);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(Waypoints);
        LOGGER.info("Saved Waypoints!");
        out.reset();
        out.close();
        fileOut.close();
        LOGGER.info("Saved waypoints to file");
        Waypoints.clear();
        return true;
    }
    public static boolean LoadWaypoints() throws IOException, ClassNotFoundException {
        String FilePath = String.format("config/lvu/Worlds/%s/", MainClient.GetPlayerWorld());
        Files.createDirectories(Paths.get(FilePath));
        FilePath = FilePath + "Waypoints.dat";
        FileInputStream fileIn = new FileInputStream(FilePath);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        if (Waypoints != null) { Waypoints.clear(); }
        LOGGER.info("Loaded Waypoints!");
        Waypoints = (HashMap<String, Waypoint>) in.readObject();
        in.reset();
        in.close();
        fileIn.close();
        LOGGER.info("Loaded Waypoints from file");

        return true;
    }

    public static int CreateWaypoint(CommandContext<FabricClientCommandSource> context) {
        String WayPointName = StringArgumentType.getString(context, "Waypoint Name");
        Waypoint waypoint = new Waypoint(context.getSource().getPlayer(), WayPointName);
        if (Waypoints == null) {
            Waypoints = new HashMap<>();
        } else if (Waypoints.containsKey(WayPointName)) {
            context.getSource().sendFeedback(Text.translatable("waypoint.exists"));
            return 0;
        }
        Text formatted = Text.of("Created waypoint '" + waypoint.getName() + "' at <" + waypoint.getX() + "," + waypoint.getY() + "," + waypoint.getZ() + ">");
        context.getSource().sendFeedback(formatted);
        Waypoints.put(waypoint.getName() ,waypoint);
        return Command.SINGLE_SUCCESS;
    }

    public static int RemoveWaypoint(CommandContext<FabricClientCommandSource> context) {
        String WaypointName = StringArgumentType.getString(context, "Waypoint Name");
        String formatted = "";
        if (Waypoints.containsKey(WaypointName)) {
            Waypoints.remove(WaypointName);
            formatted = "Waypoint: " + WaypointName + " has been removed.";
        } else {
            formatted = "Waypoint: " + WaypointName + " does not exist!";
        }
        context.getSource().sendFeedback(Text.of(formatted));
        return Command.SINGLE_SUCCESS;
    }

    public static int ClearWaypoints(CommandContext<FabricClientCommandSource> context) {
        if(Waypoints == null) { return 0; }
        Waypoints.clear();
        return  Command.SINGLE_SUCCESS;
    }
}
