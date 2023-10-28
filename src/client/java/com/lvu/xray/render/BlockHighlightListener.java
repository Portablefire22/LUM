package com.lvu.xray.render;

import com.lvu.Main;
import com.lvu.MainClient;
import com.lvu.xray.XrayMain;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BlockHighlightListener  implements WorldRenderEvents.End {
    public ArrayList<BlockEntity> BlocksToRender;
    public ArrayList<int[]> BlockCoord;
    public static ChunkPos lastChunk;
    public static Set<ChunkPos> chunks = null;
    private static VertexBuffer vertexBuffer;

    Direction[] Directions = new Direction[]{Direction.UP,Direction.DOWN,Direction.NORTH,Direction.SOUTH,Direction.WEST,Direction.EAST};
    public static int range = 2;
    //Pattern pattern = Pattern.compile("(?<=block.minecraft.)(.*)(?=_ore|_debris|_block)");
    @Override
    public void onEnd(WorldRenderContext context) {
        if(MainClient.UtilityStatus.get("xray").equals("false")) { return; }
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        //if(context.world().chunk)

        //System.out.println(chunks);
        if (PlayerMoved() && MainClient.UtilityStatus.get("xray.pause").equals("false")) {
            chunks = getchunks(context);
            BlocksToRender = new ArrayList<>();
            BlockCoord = new ArrayList<>();
            for (ChunkPos chunkPos : chunks) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = -63; y < 255; y++) {
                            BlockPos pos = new BlockPos(x, y, z);
                            BlockState block = context.world().getChunk(chunkPos.getStartPos()).getBlockState(pos);
                            if (XrayMain.shouldBlockBeRendered(block)) {
                                if (MainClient.UtilityStatus.get("xray.legit").toString().equals("true")) {
                                    int blocks = 0;
                                    if (context.world().getBlockState(new BlockPos(x + 1, y, z)).getBlock().getTranslationKey().contains("air") || context.world().getBlockState(new BlockPos(x + 1, y, z)).getBlock().getTranslationKey().contains("water") || context.world().getBlockState(new BlockPos(x + 1, y, z)).getBlock().getTranslationKey().contains("lava")) {blocks++;}
                                    else if (context.world().getBlockState(new BlockPos(x - 1, y, z)).getBlock().getTranslationKey().contains("air") || context.world().getBlockState(new BlockPos(x - 1, y, z)).getBlock().getTranslationKey().contains("water") || context.world().getBlockState(new BlockPos(x - 1, y, z)).getBlock().getTranslationKey().contains("lava")) {blocks++;}
                                    else if (context.world().getBlockState(new BlockPos(x, y + 1, z)).getBlock().getTranslationKey().contains("air") || context.world().getBlockState(new BlockPos(x, y + 1, z)).getBlock().getTranslationKey().contains("water") || context.world().getBlockState(new BlockPos(x, y + 1, z)).getBlock().getTranslationKey().contains("lava")) {blocks++;}
                                    else if (context.world().getBlockState(new BlockPos(x, y - 1, z)).getBlock().getTranslationKey().contains("air") || context.world().getBlockState(new BlockPos(x, y - 1, z)).getBlock().getTranslationKey().contains("water") || context.world().getBlockState(new BlockPos(x, y - 1, z)).getBlock().getTranslationKey().contains("lava")) {blocks++;}
                                    else if (context.world().getBlockState(new BlockPos(x, y, z + 1)).getBlock().getTranslationKey().contains("air") || context.world().getBlockState(new BlockPos(x, y, z + 1)).getBlock().getTranslationKey().contains("water") || context.world().getBlockState(new BlockPos(x, y, z + 1)).getBlock().getTranslationKey().contains("lava")) {blocks++;}
                                    else if (context.world().getBlockState(new BlockPos(x, y, z - 1)).getBlock().getTranslationKey().contains("air") || context.world().getBlockState(new BlockPos(x, y, z - 1)).getBlock().getTranslationKey().contains("water") || context.world().getBlockState(new BlockPos(x, y, z - 1)).getBlock().getTranslationKey().contains("lava")) {blocks++;}
                                    if (blocks == 0) { continue; }
                                }
                                //Chunk chnk = context.world().getChunk(chunkPos.getStartPos());
                                int offsetX = x + chunkPos.getStartX();
                                int offsetZ = z + chunkPos.getStartZ();
                                //System.out.printf("%s : %d %d %d \n", block.getBlock().getTranslationKey(), offsetX, y, offsetZ);
                                //BlockSt blockE = context.world().getBlockState(new BlockPos(offsetX, y, offsetZ));
                                //System.out.println(blockE);
                                //BlocksToRender.add(blockE);
                                BlockCoord.add(new int[]{offsetX, y, offsetZ});
                                //context.matrixStack().translate(-client.cameraEntity.getX(), -client.getCameraEntity().getY(), -client.getCameraEntity().getZ());
                            }
                        }
                    }
                }
            }
        }
        renderBox(BlockCoord, context);
        lastChunk = new ChunkPos(player.getChunkPos().x, player.getChunkPos().z);
    }

    private static void renderBox(ArrayList<int[]> BlockCoord, WorldRenderContext context) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        final Vec3d cameraPos = context.camera().getPos();
        if (BlockCoord != null) {
            vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            if(!BlockCoord.isEmpty()) {
                for (int[] pos : BlockCoord) {
                    context.matrixStack().push();
                    context.matrixStack().translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

                    RenderSystem.disableDepthTest();
                    Tessellator tessellator = Tessellator.getInstance();
                    RenderSystem.setShader(GameRenderer::getPositionColorProgram);
                    BufferBuilder bufferBuilder = tessellator.getBuffer();
                    bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);


                    Matrix4f matrix = context.matrixStack().peek().getPositionMatrix();
                    float size = 1.0f;
                    BlockState block = context.world().getBlockState(new BlockPos(pos[0],pos[1],pos[2]));

                    double DistanceMultiplier = 1 - ((Math.sqrt(Math.pow(pos[0] - player.getPos().x, 2) + Math.pow(pos[1] - player.getPos().y, 2)+ Math.pow(pos[2] - player.getPos().z, 2))))/255;
                    int opacity = 1;
                    int[] Colours = XrayMain.GetblockColour(block);
                    int red = (int) ( Colours[0] * DistanceMultiplier);
                    int green = (int) ( Colours[1] * DistanceMultiplier);
                    int blue = (int) ( Colours[2] * DistanceMultiplier);
                    if (Colours[0] != 900) {
                        bufferBuilder.vertex(matrix, pos[0], pos[1] + size, pos[2]).color(red, green, blue, opacity).next();

                        bufferBuilder.vertex(matrix, pos[0] + size, pos[1] + size, pos[2]).color(red, green, blue, opacity).next();

                        bufferBuilder.vertex(matrix, pos[0] + size, pos[1] + size, pos[2]).color(red, green, blue, opacity).next();

                        bufferBuilder.vertex(matrix, pos[0] + size, pos[1] + size, pos[2] + size).color(red, green, blue, opacity).next();

                        bufferBuilder.vertex(matrix, pos[0] + size, pos[1] + size, pos[2] + size).color(red, green, blue, opacity).next();

                        bufferBuilder.vertex(matrix, pos[0], pos[1] + size, pos[2] + size).color(red, green, blue, opacity).next();

                        bufferBuilder.vertex(matrix, pos[0], pos[1] + size, pos[2] + size).color(red, green, blue, opacity).next();

                        bufferBuilder.vertex(matrix, pos[0], pos[1] + size, pos[2]).color(red, green, blue, opacity).next();

                        // BOTTOM

                        bufferBuilder.vertex(matrix, pos[0] + size, pos[1], pos[2]).color(red, green, blue, opacity).next();

                        bufferBuilder.vertex(matrix, pos[0] + size, pos[1], pos[2] + size).color(red, green, blue, opacity).next();

                        bufferBuilder.vertex(matrix, pos[0] + size, pos[1], pos[2] + size).color(red, green, blue, opacity).next();

                        bufferBuilder.vertex(matrix, pos[0], pos[1], pos[2] + size).color(red, green, blue, opacity).next();

                        bufferBuilder.vertex(matrix, pos[0], pos[1], pos[2] + size).color(red, green, blue, opacity).next();

                        bufferBuilder.vertex(matrix, pos[0], pos[1], pos[2]).color(red, green, blue, opacity).next();

                        bufferBuilder.vertex(matrix, pos[0], pos[1], pos[2]).color(red, green, blue, opacity).next();

                        bufferBuilder.vertex(matrix, pos[0] + size, pos[1], pos[2]).color(red, green, blue, opacity).next();

                        // Edge 1

                        bufferBuilder.vertex(matrix, pos[0] + size, pos[1], pos[2] + size).color(red, green, blue, opacity).next();

                        bufferBuilder.vertex(matrix, pos[0] + size, pos[1] + size, pos[2] + size).color(red, green, blue, opacity).next();

                        // Edge 2

                        bufferBuilder.vertex(matrix, pos[0] + size, pos[1], pos[2]).color(red, green, blue, opacity).next();

                        bufferBuilder.vertex(matrix, pos[0] + size, pos[1] + size, pos[2]).color(red, green, blue, opacity).next();

                        // Edge 3

                        bufferBuilder.vertex(matrix, pos[0], pos[1], pos[2] + size).color(red, green, blue, opacity).next();

                        bufferBuilder.vertex(matrix, pos[0], pos[1] + size, pos[2] + size).color(red, green, blue, opacity).next();

                        // Edge 4

                        bufferBuilder.vertex(matrix, pos[0], pos[1], pos[2]).color(red, green, blue, opacity).next();

                        bufferBuilder.vertex(matrix, pos[0], pos[1] + size, pos[2]).color(red, green, blue, opacity).next();
                    }

                    tessellator.draw();
                    tessellator.getBuffer().clear();
                    bufferBuilder.clear();
                    context.matrixStack().pop();
                    RenderSystem.enableDepthTest();
                }
            }
        }
    }

    private static boolean PlayerMoved() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return false;
        }
        return lastChunk == null || lastChunk.x != player.getChunkPos().x
                || lastChunk.z != player.getChunkPos().z;
    }

    private static Set<ChunkPos> getchunks(WorldRenderContext context) {
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