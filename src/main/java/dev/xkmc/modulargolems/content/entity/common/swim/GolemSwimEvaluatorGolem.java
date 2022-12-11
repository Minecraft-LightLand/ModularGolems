package dev.xkmc.modulargolems.content.entity.common.swim;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;

public class GolemSwimEvaluatorGolem extends AmphibiousNodeEvaluator {

	public GolemSwimEvaluatorGolem(boolean pPrefersShallowSwimming) {
		super(pPrefersShallowSwimming);
	}

	@Override
	protected double getFloorLevel(BlockPos pPos) {
		return this.mob.isInWater() ? (double)pPos.getY() + 0.5D : super.getFloorLevel(pPos);
	}
}
