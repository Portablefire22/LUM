package com.lvu.mixin.client;

import com.lvu.Main;
import com.lvu.waypoint.Waypoint;
import com.lvu.waypoint.WaypointManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Mixin({PlayerEntity.class, ServerPlayerEntity.class})
public class OnDeathMixin {
    @Inject(method = "onDeath", at= @At("HEAD"))
    private void OnDeath(DamageSource damageSource, CallbackInfo ci) {
        if (WaypointManager.Waypoints == null) {
            WaypointManager.Waypoints = new HashMap<>();
        }
        List<String> deaths = WaypointManager.Waypoints.keySet().stream().filter(contains("Death")).collect(Collectors.toList());
        String WaypointName = "Death #" + deaths.size();
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        Main.LOGGER.info(WaypointName);
        assert player != null;
        WaypointManager.Waypoints.put(WaypointName, new Waypoint(player, WaypointName, 255, 1 , 1));
        player.sendMessage(Text.of("Created: " + WaypointName + " at <" + player.getBlockX() + "," + player.getBlockY() + "," + player.getBlockZ() + ">."));
    }

    private static Predicate<String> contains(String contained) {
        return s -> s.contains(contained);
    }
}

