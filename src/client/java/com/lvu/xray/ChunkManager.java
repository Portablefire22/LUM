package com.lvu.xray;

import com.lvu.MainClient;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.ChunkPos;

import java.util.HashSet;
import java.util.Set;

import static com.lvu.xray.render.Render.range;

public class ChunkManager  {
    static Set<ChunkPos> getchunks(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        //System.out.println(player);
        if (player != null) {
            range = Integer.parseInt(MainClient.UtilityStatus.get("xray.range").toString());
            int cX = player.getChunkPos().x;
            int cZ = player.getChunkPos().z;
            //    int skippedChunks = 0;
            //System.out.printf("cX: &d | cZ: &d%n", cX, cZ);
            Set<ChunkPos> chunks = new HashSet<>();
            for (int i = cX - range; i <= cX + range; i++) {
                //System.out.println(String.format("i: &d", i));
                for (int j = cZ - range; j <= cZ + range; j++) {
                    //System.out.println(String.format("j: &d", j));
                    ChunkPos chunk = new ChunkPos(i, j);
                    chunks.add(chunk);
                }
            }
            return chunks;
        }
        return new HashSet<>();
    }
}
