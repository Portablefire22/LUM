package com.lvu.mixin.client;

import com.lvu.xray.BlockManager;
import com.lvu.xray.Xray;
import com.lvu.xray.chunk.XrayChunkManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashMap;

@Mixin(Block.class)
public class BlockHighlighterMixin {
    @Inject(at = @At("TAIL"), method = "shouldDrawSide", cancellable = true)
    private static void render(BlockState state, BlockView world, BlockPos pos, Direction side, BlockPos otherPos, CallbackInfoReturnable<Boolean> cir) {
        if (Xray.shouldBlockBeRendered(state)) {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            int i = 0;
            assert MinecraftClient.getInstance().player != null;
            World cWorld = MinecraftClient.getInstance().player.getWorld();
            ChunkPos chunkPos = cWorld.getChunk(pos).getPos();
            XrayChunkManager.ChunkMap.computeIfAbsent(chunkPos, k -> new HashMap<>());
            ArrayList<int[]> blocks = new ArrayList<>();
            String blockName = state.getBlock().getTranslationKey();
            if (XrayChunkManager.ChunkMap.get(chunkPos).containsKey(blockName)) {
                blocks = XrayChunkManager.ChunkMap.get(chunkPos).get(blockName);
                //blocks.add(new int[]{pos.getX(), pos.getY(), pos.getZ(), i});
            }
            if (BlockManager.isVisible((ClientWorld) cWorld, x,y,z)) { i = 1; }
            blocks.add(new int[]{pos.getX(), pos.getY(), pos.getZ(), i });
            XrayChunkManager.ChunkMap.get(chunkPos).put(blockName,blocks);
        }
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

