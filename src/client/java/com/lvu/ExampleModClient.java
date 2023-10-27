package com.lvu;

import com.lvu.render.BlockHighlightListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.mixin.blockrenderlayer.RenderLayersMixin;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayers;

public class ExampleModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		WorldRenderEvents.END.register(new BlockHighlightListener());
	}



	public static boolean shouldBlockBeRendered(BlockState state){
		return state.getBlock().getTranslationKey().contains("ore") || state.getBlock().getTranslationKey().contains("frame");
    }
}