package dev.xkmc.modulargolems.content.entity.mode;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.world.entity.Entity;

public class PatrolMode extends GolemMode {

	protected PatrolMode(boolean positioned, boolean movable, boolean wander, MGLangData lang, MGLangData name) {
		super(positioned, movable, wander, lang, name);
	}

	@Override
	public double getStartFollowDistance(AbstractGolemEntity<?, ?> golem) {
		return 0;
	}

	@Override
	public void tick(AbstractGolemEntity<?, ?> golem) {
		Entity e = golem;
		if (e.isPassenger()) e = e.getRootVehicle();
		if (!e.isInWater() && !e.onGround()) return;
		if (golem.getNavigation().isDone()) {
			golem.advancePatrolStage();
		}
	}

	@Override
	public double getStopDistance() {
		return 0;
	}
}
