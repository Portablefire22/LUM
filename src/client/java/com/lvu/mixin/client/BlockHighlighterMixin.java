package com.lvu.mixin.client;

import com.lvu.ExampleModClient;
import com.lvu.render.BlockHighlightListener;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockHighlighterMixin {
    @Unique
    static ClientPlayerEntity player = MinecraftClient.getInstance().player;
    @Inject(at = @At("HEAD"), method = "shouldDrawSide", cancellable = true)
    private static void render(BlockState state, BlockView world, BlockPos pos, Direction side, BlockPos otherPos, CallbackInfoReturnable<Boolean> cir) {
        /*if(ExampleModClient.shouldBlockBeRendered(state)){
            VertexConsumer consumer = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers().getBuffer(RenderLayer.getLines());
            WorldRenderer.drawBox(
                    otherPos,
                    consumer,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    otherPos.getX(),
                    otherPos.getY(),
                    otherPos.getZ()

            );
        }*/
    }
}

