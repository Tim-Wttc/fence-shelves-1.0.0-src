package dev.fenceshelves.mixin;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.ShelfBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Cosmetic fix. Once shelves are in {@code #minecraft:fences}, a nether brick fence (the one fence
 * that is not in {@code #minecraft:wooden_fences}) would treat a neighbouring shelf as "the same
 * kind of fence" and grow an arm into it. Fences should still look the way they always did, so a
 * shelf only connects when its face is solid, exactly as before.
 *
 * <p>{@code require = 0}: if the method signature ever changes, the mod keeps working and only
 * loses this cosmetic tweak instead of crashing the game.
 */
@Mixin(FenceBlock.class)
public abstract class FenceBlockMixin {
	@Inject(
			method = "connectsTo(Lnet/minecraft/world/level/block/state/BlockState;ZLnet/minecraft/core/Direction;)Z",
			at = @At("RETURN"),
			cancellable = true,
			require = 0
	)
	private void fenceShelves$dontConnectToShelves(BlockState state, boolean isSideSolid, Direction direction,
												   CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValueZ() && !isSideSolid && state.getBlock() instanceof ShelfBlock) {
			cir.setReturnValue(false);
		}
	}
}
