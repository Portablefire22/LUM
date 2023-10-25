package com.lvu;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.block.BlockState;

public class ExampleModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
	}



	public static boolean shouldBlockBeRendered(BlockState state){
		if (state.getBlock().getTranslationKey().contains("ore")){
			System.out.println(state.getBlock().getTranslationKey());
			return state.getBlock().getTranslationKey().contains("ore");
		}
		return false;
    }
}