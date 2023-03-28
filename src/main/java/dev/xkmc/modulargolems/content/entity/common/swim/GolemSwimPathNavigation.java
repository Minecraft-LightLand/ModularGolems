package dev.xkmc.modulargolems.content.entity.common.swim;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import org.jetbrains.annotations.Nullable;

public class GolemSwimPathNavigation extends AmphibiousPathNavigation {

	public GolemSwimPathNavigation(Mob p_217788_, Level p_217789_) {
		super(p_217788_, p_217789_);
	}

	protected PathFinder createPathFinder(int p_217792_) {
		this.nodeEvaluator = new GolemSwimEvaluatorGolem(false);
		this.nodeEvaluator.setCanPassDoors(true);
		return new PathFinder(this.nodeEvaluator, p_217792_);
	}

	@Nullable
	@Override
	public Path createPath(Entity pEntity, int pAccuracy) {
		return this.createPath(ImmutableSet.of(pEntity.blockPosition()), 16, !pEntity.isInWater(), pAccuracy);
	}
}
