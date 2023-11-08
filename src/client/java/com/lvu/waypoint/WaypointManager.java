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
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;

import java.io.*;
import java.util.*;

import static com.lvu.Main.LOGGER;

public class WaypointManager {
    public static HashMap<String, Waypoint> Waypoints;
    public void WaypointCommand(CommandContext<FabricClientCommandSource> context) {

    }

    // TODO Save waypoints to a file and load them on world load.

    public static boolean SaveWaypoints() throws IOException {
        if (Waypoints == null) return false;
        if (Waypoints.isEmpty()) return true;
        FileOutputStream fileOut = new FileOutputStream("config/lvu/Waypoints.dat");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(Waypoints);
        out.reset();
        out.close();
        fileOut.close();
        LOGGER.info("Saved waypoints to file");

        return true;
    }
    public static boolean LoadWaypoints() throws IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream("config/lvu/Waypoints.dat");
        ObjectInputStream in = new ObjectInputStream(fileIn);
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
