package com.lvu.mixin.client;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class LuminMixin {
    @Shadow
    public abstract Block getBlock();

    @Inject(at = @At("HEAD"), method = "getLuminance", cancellable = true)
    private void getLuminance(CallbackInfoReturnable<Integer> cir) {
        // This code is injected into the rendering of every block
        cir.setReturnValue(12);
        cir.cancel();
        /*if (ExampleModClient.shouldBlockBeRendered(state)) {
            cir.setReturnValue(true);
        } else {
            cir.setReturnValue(false);
        }*/
    }

    @Inject(at = @At("HEAD"), method = "getAmbientOcclusionLightLevel", cancellable = true)
    private void getAmbientOcclusionLightLevel(CallbackInfoReturnable<Float> cir) {
        // This code is injected into the rendering of every block
        cir.setReturnValue(1.0f);
        cir.cancel();
        /*if (ExampleModClient.shouldBlockBeRendered(state)) {
            cir.setReturnValue(true);
        } else {
            cir.setReturnValue(false);
        }*/
    }
}
