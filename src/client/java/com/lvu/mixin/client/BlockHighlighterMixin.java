package com.lvu.mixin.client;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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

