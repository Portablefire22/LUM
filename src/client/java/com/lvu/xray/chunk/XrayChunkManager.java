package com.lvu.xray.chunk;

import com.lvu.Main;
import com.lvu.MainClient;
import com.lvu.xray.BlockManager;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.lvu.xray.render.Render.range;

public class XrayChunkManager {
    public static HashMap<ChunkPos, HashMap<String, ArrayList<int[]>>> ChunkMap = new HashMap<>();
    public static Set<ChunkPos> getchunks(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        //System.out.println(player);
        if (player != null) {
            range = Integer.parseInt(MainClient.UtilityStatus.get("xray.range").toString());
            int cX = player.getChunkPos().x;
            Set<ChunkPos> chunks = getChunkPosSet(player, cX);
            return chunks;
        }
        return new HashSet<>();
    }

    @NotNull
    private static Set<ChunkPos> getChunkPosSet(ClientPlayerEntity player, int cX) {
        int cZ = player.getChunkPos().z;
        //    int skippedChunks = 0;
        //System.out.printf("cX: &d | cZ: &d%n", cX, cZ);
        Set<ChunkPos> chunks = new HashSet<>();
        for (int i = cX - range; i <= cX + range; i++) {
            //System.out.println(String.format("i: &d", i));
            for (int j = cZ - range; j <= cZ + range; j++) {
                //System.out.println(String.format("j: &d", j));
                //Main.LOGGER.info("[" + i + "," + j + "]");
                ChunkPos chunk = new ChunkPos(i, j);
                HashMap<String, ArrayList<int[]>> chunkResult = ChunkMap.get(chunk);
                if  (chunkResult == null) {
                    ChunkToHash(player.clientWorld, (WorldChunk) player.clientWorld.getChunk(chunk.getStartPos()));
                } /*else {
                    Main.LOGGER.info("Chunk Result: "  + String.valueOf(chunkResult));
                }*/
                chunks.add(chunk);
            }
        }
        return chunks;
    }

    /*
    TODO
    Using the chunk load event, I should be able to run through the newly generated chunk and store it in a hashmap.
    The chunk hashmap could store the block as a key and every coordinate of that block in an array.
    Loop the array, chunk only gets searched once instead of everytime you move.

    Pros:
        - No lag spikes from moving around if you are in a chunk you have already visited
    Cons:
        - Storage space could get fucked if not done correctly


    Currently the initial range does not ever get loaded. fix this.
     */

    public static void ChunkToHash(ClientWorld world, WorldChunk chunk) {
        if (!ChunkMap.containsKey(chunk.getPos())) {
            //Main.LOGGER.info("Converting : " + chunk.getPos());
            if (ChunkMap.get(chunk.getPos()) != null) {
                HashMap<String, ArrayList<int[]>> blocks = BlockManager.BlocksToHash(world, chunk);
                if (!blocks.containsKey("VOID")){
                    ChunkMap.put(chunk.getPos(), blocks);
                }
            }
            try {
                //SaveChunksToFile();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public static void SaveChunksToFile() throws IOException {
        FileOutputStream fileOut = new FileOutputStream("config/lvu/Chunks.ser");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(ChunkMap);
        out.close();
        fileOut.close();
    }
}
