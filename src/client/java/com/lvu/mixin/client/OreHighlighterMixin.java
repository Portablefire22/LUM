package com.lvu.mixin.client;

import com.lvu.ExampleModClient;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(Block.class)
public abstract class OreHighlighterMixin {

    @Inject(at = @At("HEAD"), method = "shouldDrawSide", cancellable = true)
    private static void shouldDrawSide(BlockState state, BlockView world, BlockPos pos, Direction side, BlockPos otherPos, CallbackInfoReturnable<Boolean> cir) {
        // This code is injected into the rendering of every block
        cir.setReturnValue(ExampleModClient.shouldBlockBeRendered(state));
        cir.cancel();
        /*if (ExampleModClient.shouldBlockBeRendered(state)) {
            cir.setReturnValue(true);
        } else {
            cir.setReturnValue(false);
        }*/
    }
}

