package dev.fenceshelves;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Shared helper for the shelf-as-fence behaviour.
 *
 * <p>Fences are 24/16 = 1.5 blocks tall for collision, which is what stops players and most mobs
 * from jumping onto or over them. Shelves get the same collision height here.
 */
public final class FenceShelves {
	/** Vanilla fence collision height, in blocks (24 pixels). */
	public static final double FENCE_COLLISION_HEIGHT = 1.5;

	/**
	 * Raised shapes keyed by the original shape instance (identity, weak). Shelf shapes are shared
	 * constants, so this stays tiny, and weak keys mean nothing leaks if a shape is ever created
	 * on the fly.
	 */
	private static final Cache<VoxelShape, VoxelShape> RAISED = CacheBuilder.newBuilder().weakKeys().build();

	private FenceShelves() {
	}

	/**
	 * Returns the shelf's own collision footprint, extended up to fence height.
	 * Empty shapes are returned unchanged.
	 */
	public static VoxelShape raiseToFenceHeight(VoxelShape original) {
		if (original.isEmpty()) {
			return original;
		}

		VoxelShape cached = RAISED.getIfPresent(original);
		if (cached != null) {
			return cached;
		}

		VoxelShape raised = Shapes.box(
				original.min(Direction.Axis.X),
				original.min(Direction.Axis.Y),
				original.min(Direction.Axis.Z),
				original.max(Direction.Axis.X),
				Math.max(original.max(Direction.Axis.Y), FENCE_COLLISION_HEIGHT),
				original.max(Direction.Axis.Z)
		);
		RAISED.put(original, raised);
		return raised;
	}
}
