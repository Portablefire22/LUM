package com.lvu.xray.render;

import com.lvu.MainClient;
import com.lvu.xray.BlockManager;
import com.lvu.xray.Xray;
import com.lvu.xray.chunk.XrayChunkManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.BlockState;
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
import java.util.Set;

public class XrayRender {
    public static ChunkPos lastChunk;

    Direction[] Directions = new Direction[]{Direction.UP,Direction.DOWN,Direction.NORTH,Direction.SOUTH,Direction.WEST,Direction.EAST};
    public static int range = 2;
    static ArrayList<int[]> BlockCoord = null;
    //Pattern pattern = Pattern.compile("(?<=block.minecraft.)(.*)(?=_ore|_debris|_block)");


    public static void Render(WorldRenderContext context) {
        if(MainClient.UtilityStatus.get("xray").equals("false")) { return; }
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (PlayerMoved() && MainClient.UtilityStatus.get("xray.pause").equals("false")) {
            Set<ChunkPos> chunks = XrayChunkManager.getchunks();
            if (MainClient.UtilityStatus.get("xray.experimentalsearch").equals("false")){
                BlockCoord = BlockManager.GetBlocks(context, chunks);
            } else {
                BlockCoord = BlockManager.GetBlocksHashSearch(context, chunks);
            }
        }
        renderBox(BlockCoord, context);
        lastChunk = new ChunkPos(player.getChunkPos().x, player.getChunkPos().z);
    }

    // Black Magic
    private static void renderBox(ArrayList<int[]> BlockCoord, WorldRenderContext context) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        final Vec3d cameraPos = context.camera().getPos();
        if (BlockCoord != null) {
            VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
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
                    int[] Colours = Xray.GetBlockColour(block);
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
                    //tessellator.getBuffer().clear();
                    //bufferBuilder.clear();
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
}