package com.lvu.waypoint;

import com.lvu.MainClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.WorldSavePath;


import java.io.Serializable;

public class Waypoint implements Serializable {

    // Highly doubt the coordinate will ever exceed the max integer
    private int x;
    private int y;
    private int z;

    private String dimension;
    private String world;
    private boolean render;

    private String name;

    private int red;
    private int green;
    private int blue;

    // constructor
    public Waypoint(int x, int y, int z, String name) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.render = true;
        assert MinecraftClient.getInstance().player != null;
        this.dimension = String.valueOf(MinecraftClient.getInstance().player.getEntityWorld().getRegistryKey().getValue().toString());
        this.world = MainClient.GetPlayerWorld();

        this.red = (int) Math.floor(Math.random() * 255);
        this.green = (int) Math.floor(Math.random() * 255);
        this.blue = (int) Math.floor(Math.random() * 255);
    }

    public Waypoint(int x, int y, int z, String name, int red, int green, int blue) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.render = true;
        assert MinecraftClient.getInstance().player != null;
        this.dimension = String.valueOf(MinecraftClient.getInstance().player.getEntityWorld().getRegistryKey().getValue().toString());
        this.world = MainClient.GetPlayerWorld();
    }

    public Waypoint(ClientPlayerEntity player, String name) {
        this.x = player.getBlockX();
        this.y = player.getBlockY();
        this.z = player.getBlockZ();
        this.name = name;
        this.red = (int) Math.floor(Math.random() * 255);
        this.green = (int) Math.floor(Math.random() * 255);
        this.blue = (int) Math.floor(Math.random() * 255);
        this.render = true;
        assert MinecraftClient.getInstance().player != null;
        this.dimension = String.valueOf(player.getEntityWorld().getRegistryKey().getValue().toString().toString());
        this.world = MainClient.GetPlayerWorld();
    }

    public Waypoint(ClientPlayerEntity player, String name, int red, int green, int blue) {
        this.x = player.getBlockX();
        this.y = player.getBlockY();
        this.z = player.getBlockZ();
        this.name = name;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.render = true;
        assert MinecraftClient.getInstance().player != null;
        this.dimension = String.valueOf(player.getEntityWorld().getRegistryKey().getValue().toString());
        this.world = MainClient.GetPlayerWorld();
    }

    // getter
    public String getName() { return name; }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }


    public int getZ() {
        return this.z;
    }

    public int getRed() {
        return this.red;
    }

    public int getGreen() {
        return this.green;
    }

    public int getBlue() {
        return this.blue;
    }
    public String getDimension() { return this.dimension; }
    public String getWorld() { return this.world; }
    public boolean isRender() { return this.render; }
    // setter
    public void setName(String name) { this.name = name; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setZ(int z) { this.z = z; }
    public void setRed(int red) { this.red = red; }
    public void setGreen(int green) { this.green = green; }
    public void setBlue(int blue) { this.blue = blue; }
    public void setDimension(String dimension) { this.dimension = dimension; }
    public void setWorld(String world) { this.world = world; }
    public void setRender(boolean render) { this.render = render; }
}

