package com.lvu.waypoint;

import com.lvu.Main;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class WaypointRender {

    private static final BufferBuilder Buffer1 = new BufferBuilder(2000000);

    /* TODO
    Add a system to show waypoint name and distance when the waypoint is hovered.
     */

    public static void Render(WorldRenderContext context) {
        if (WaypointManager.Waypoints == null) {return;}
        MatrixStack matrixStack = context.matrixStack();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        /*GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glDepthMask(false); // if true, can't see entities and water behind the beam
        GL11.glEnable(GL11.GL_BLEND); // if not enabled, beam is basically black
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);*/

        double cameraX = context.camera().getPos().x;
        double cameraY = context.camera().getPos().y;
        double cameraZ = context.camera().getPos().z;

        matrixStack.translate(-cameraX, -cameraY, -cameraZ);

        float size = 0.25f;
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.polygonOffset(-3f, -3f);
        RenderSystem.enablePolygonOffset();
        RenderSystem.enableBlend();
        float opacity = 0.2f;
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        float maxY = 500;
        float minY = -63;
        for (Waypoint waypoint: WaypointManager.Waypoints.values()) {
            double centreX = waypoint.getX() + 0.5;
            double centreZ = waypoint.getZ() + 0.5;
            //Main.LOGGER.info("Rendering: " + waypoint.getName() + " at : <" + waypoint.getX() + "," + waypoint.getY() + "," + waypoint.getZ() + "> " + "<" + waypoint.getRed() + "," + waypoint.getGreen() + "," + waypoint.getBlue() + ">");

            float minX = (float) (centreX - size) ;
            float maxX = (float) (centreX + size);



            float minZ = (float) (centreZ);
            float maxZ = (float) (centreZ + 0.25f);

            float red = (float) waypoint.getRed() / 255;
            float green = (float) waypoint.getGreen() / 255;
            float blue = (float) waypoint.getBlue() / 255;

            Matrix4f matrix = context.matrixStack().peek().getPositionMatrix();

            buffer.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, opacity).next();
            buffer.vertex(matrix, minX, minY, maxZ).color(red, green, blue, opacity).next();
            buffer.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, opacity).next();
            buffer.vertex(matrix, maxX , maxY, maxZ).color(red, green, blue, opacity).next();

            minZ -= size;
            buffer.vertex(matrix, minX, maxY, minZ).color(red , green, blue, opacity).next();
            buffer.vertex(matrix, minX, minY, minZ).color(red, green, blue, opacity).next();
            buffer.vertex(matrix, maxX, minY, minZ).color(red, green, blue, opacity).next();
            buffer.vertex(matrix, maxX , maxY, minZ).color(red, green, blue, opacity).next();

            buffer.vertex(matrix, minX, maxY, minZ).color(red , green, blue, opacity).next();
            buffer.vertex(matrix, minX, minY, minZ).color(red, green, blue, opacity).next();
            buffer.vertex(matrix, minX, minY, maxZ).color(red, green, blue, opacity).next();
            buffer.vertex(matrix, minX , maxY, maxZ).color(red, green, blue, opacity).next();

            buffer.vertex(matrix, maxX, maxY, minZ).color(red , green, blue, opacity).next();
            buffer.vertex(matrix, maxX, minY, minZ).color(red, green, blue, opacity).next();
            buffer.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, opacity).next();
            buffer.vertex(matrix, maxX , maxY, maxZ).color(red, green, blue, opacity).next();

        }

        tessellator.draw();
        RenderSystem.polygonOffset(0f, 0f);
        RenderSystem.disablePolygonOffset();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.disableBlend();
    }
}
