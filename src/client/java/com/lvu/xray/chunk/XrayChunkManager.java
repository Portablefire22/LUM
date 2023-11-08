package com.lvu.xray.chunk;

import com.lvu.Main;
import com.lvu.MainClient;
import com.lvu.xray.BlockManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.lvu.xray.render.XrayRender.range;

public class XrayChunkManager {
    public static HashMap<ChunkPos, HashMap<String, ArrayList<int[]>>> ChunkMap = new HashMap<>();
    public static Set<ChunkPos> getchunks() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        //System.out.println(player);
        if (player != null) {
            range = Integer.parseInt(MainClient.UtilityStatus.get("xray.range").toString());
            Set<ChunkPos> chunks = getChunkPosSet(player);
            return chunks;
        }
        return new HashSet<>();
    }

    @NotNull
    private static Set<ChunkPos> getChunkPosSet(ClientPlayerEntity player) {
        int cZ = player.getChunkPos().z;
        int cX = player.getChunkPos().x;
        Main.LOGGER.info("cX: {} | cZ: {}", cX, cZ);
        //    int skippedChunks = 0;
        //System.out.printf("cX: &d | cZ: &d%n", cX, cZ);
        Set<ChunkPos> chunks = new HashSet<>();
        for (int i = cX - range; i <= cX + range; i++) {
            //System.out.println(String.format("i: &d", i));
            for (int j = cZ - range; j <= cZ + range; j++) {
                //System.out.println(String.format("j: &d", j));

                ChunkPos chunk = new ChunkPos(i, j);
                HashMap<String, ArrayList<int[]>> chunkResult = ChunkMap.get(chunk);
                //Main.LOGGER.info("[" + i + "," + j + "] = " + chunkResult);
                if  (chunkResult == null) {
                    ChunkToHash(player.getWorld(), player.getWorld().getChunk(chunk.x, chunk.z));
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

    Using portals kinda fucks with this if the chunk was not previously loaded.
    Also think there may be a problem with overlapping chunks with differing dimensions.
        - Has not occurred during my server playthrough
     */

    public static void ChunkToHash(World world, WorldChunk chunk) {
        if (!ChunkMap.containsKey(chunk.getPos())) {
            //Main.LOGGER.info("Converting : " + chunk.getPos());
            if (ChunkMap.get(chunk.getPos()) == null) {
                HashMap<String, ArrayList<int[]>> blocks = BlockManager.BlocksToHash((ClientWorld) world, chunk);
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
        HashMap<int[], HashMap<String, ArrayList<int[]>>> SaveMap = new HashMap<>();
        for(ChunkPos chunkpos: ChunkMap.keySet() ) {
            SaveMap.put(new int[]{chunkpos.x,chunkpos.z}, ChunkMap.get(chunkpos));
        }
        out.writeObject(SaveMap);
        out.reset();
        out.close();
        fileOut.close();
    }

    public static void LoadChunksFromFile() throws IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream("config/lvu/Chunks.ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        HashMap<int[], HashMap<String, ArrayList<int[]>>> SaveMap = (HashMap<int[], HashMap<String, ArrayList<int[]>>>) in.readObject();
        for(int[] intArr: SaveMap.keySet() ) {
            ChunkMap.put(new ChunkPos(intArr[0], intArr[1]), SaveMap.get(intArr));
        }
        in.reset();
        in.close();
        fileIn.close();
    }
}
