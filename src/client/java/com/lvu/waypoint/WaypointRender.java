package com.lvu.waypoint;

import com.lvu.Main;
import com.lvu.MainClient;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.Objects;

public class WaypointRender {

    private static final BufferBuilder Buffer1 = new BufferBuilder(2000000);

    /* TODO
    Add a system to show waypoint name and distance when the waypoint is hovered.
     */

    public static void Render(WorldRenderContext context) {
        if (WaypointManager.Waypoints == null) {return;}
        if (WaypointManager.Waypoints.isEmpty()) { return; }
        boolean HasRendered = false;
        float size = 0.25f;
        float opacity = 0.2f;
        float maxY = 500;
        float minY = -63;
        MatrixStack matrixStack = context.matrixStack();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        for (Waypoint waypoint: WaypointManager.Waypoints.values()) {
            if (!Objects.equals(waypoint.getDimension(), context.world().getRegistryKey().getValue().toString()) || !Objects.equals(waypoint.getWorld(), MainClient.GetPlayerWorld())) continue;
            if (!HasRendered) {
                double cameraX = context.camera().getPos().x;
                double cameraY = context.camera().getPos().y;
                double cameraZ = context.camera().getPos().z;
                RenderSystem.disableCull();
                RenderSystem.enableDepthTest();
                RenderSystem.depthMask(false);
                RenderSystem.polygonOffset(-3f, -3f);
                RenderSystem.enablePolygonOffset();
                RenderSystem.enableBlend();

                /*VertexConsumerProvider vertexConsumerProvider = VertexConsumerProvider.immediate(buffer);*/
                matrixStack.push();
                matrixStack.translate(-cameraX, -cameraY, -cameraZ);
                RenderSystem.setShader(GameRenderer::getPositionColorProgram);
                buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
                HasRendered = true;
            }

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

            Matrix4f matrix = matrixStack.peek().getPositionMatrix();

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
        if (HasRendered) {
            tessellator.draw();
            tessellator.getBuffer().clear();
            VertexConsumerProvider.Immediate vertProv = VertexConsumerProvider.immediate(tessellator.getBuffer());
            for (Waypoint waypoint : WaypointManager.Waypoints.values()) {
                if (!Objects.equals(waypoint.getDimension(), context.world().getRegistryKey().getValue().toString()) || !Objects.equals(waypoint.getWorld(), MainClient.GetPlayerWorld()))
                    continue;
                RenderWaypointName(context, vertProv, waypoint);
            }
            vertProv.draw();
            matrixStack.pop();
            tessellator.getBuffer().clear();
            RenderSystem.polygonOffset(0f, 0f);
            RenderSystem.disablePolygonOffset();
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();
        }
    }

    private  static  void RenderWaypointName(WorldRenderContext context, VertexConsumerProvider vertProv, Waypoint waypoint) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        Vec3d CameraPos = context.camera().getPos();
        MatrixStack matrixStack = context.matrixStack();
        matrixStack.push();
        String WaypointName = waypoint.getName();
        double s = CalcDistance(CameraPos, new Vec3d(waypoint.getX(), waypoint.getY(), waypoint.getZ()));
        String formatted = new DecimalFormat("#.#").format(s) + "m";
        int textWidth = -textRenderer.getWidth(WaypointName);
        float xPos = textWidth / 2.0f;
        float xPos2 = -textRenderer.getWidth(formatted) / 2.0f;
        double centreX = waypoint.getX() + 0.5;
        double centreZ = waypoint.getZ() + 0.5;
        matrixStack.translate(centreX, waypoint.getY() + 1, centreZ);
        matrixStack.multiply(context.camera().getRotation());

        float size = Math.max((float) (s / 5), 1);
        matrixStack.scale(size, size, size);
        matrixStack.scale(-0.025F, -0.025F, 0.025F);
        matrixStack.translate(0,-20,0);
        Matrix4f PositionMatrix = matrixStack.peek().getPositionMatrix();
        float g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
        int j = (int)(g * 255.0F) << 24;

        textRenderer.draw(WaypointName, xPos, 0 , 0xFFFFFF, false, PositionMatrix, vertProv, TextRenderer.TextLayerType.SEE_THROUGH, j, LightmapTextureManager.MAX_LIGHT_COORDINATE);
        textRenderer.draw(formatted, xPos2, 10 , 0xFFFFFF, false, PositionMatrix, vertProv, TextRenderer.TextLayerType.SEE_THROUGH, j, LightmapTextureManager.MAX_LIGHT_COORDINATE);
        matrixStack.pop();
    }

    private static double CalcDistance(Vec3d CameraPos, Vec3d WaypointPos) {
        return Math.sqrt( Math.pow(CameraPos.x - WaypointPos.x , 2) + Math.pow(CameraPos.y - WaypointPos.y , 2) + Math.pow(CameraPos.z - WaypointPos.z , 2));
    }

}
