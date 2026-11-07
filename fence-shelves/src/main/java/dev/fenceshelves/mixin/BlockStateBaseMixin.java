package dev.fenceshelves.mixin;

import dev.fenceshelves.FenceShelves;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.ShelfBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Raises the collision box of every tagged shelf to fence height.
 *
 * <p>Injecting into {@code BlockStateBase} (instead of ShelfBlock) means it works no matter which
 * class in the hierarchy supplies the shelf's collision shape, including modded ShelfBlock
 * subclasses. A shelf only counts when it is in {@code #minecraft:fences}, so a data pack can
 * switch the behaviour off for a given shelf by removing it from that tag.
 */
@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {
	@Inject(
			method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
			at = @At("RETURN"),
			cancellable = true
	)
	private void fenceShelves$raiseShelfCollision(BlockGetter level, BlockPos pos, CollisionContext context,
												   CallbackInfoReturnable<VoxelShape> cir) {
		// BlockState is the only concrete subclass of BlockStateBase.
		BlockState self = (BlockState) (Object) this;

		// instanceof first: it is the cheap check and keeps every non-shelf block off the slow path.
		if (self.getBlock() instanceof ShelfBlock && self.is(BlockTags.FENCES)) {
			cir.setReturnValue(FenceShelves.raiseToFenceHeight(cir.getReturnValue()));
		}
	}
}
