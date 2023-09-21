package dev.xkmc.modulargolems.compat.materials.l2complements;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class EnderTeleportGoal extends Goal {

	private static final int INIT_CD = 20, SUCCESS_CD = 100, FAIL_CD = 20, DIST = 6;

	private final AbstractGolemEntity<?, ?> entity;

	private int coolDown = 0;

	public EnderTeleportGoal(AbstractGolemEntity<?, ?> entity) {
		this.entity = entity;
	}

	@Override
	public boolean canUse() {
		return entity.getTarget() != null && !entity.getNavigation().isDone() &&
				(entity.getNavigation().isStuck() || entity.distanceToSqr(entity.getTarget()) > DIST * DIST);
	}

	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void start() {
		coolDown = INIT_CD;
	}

	@Override
	public void stop() {
		coolDown = 0;
	}

	public void tick() {
		if (coolDown > 0) {
			coolDown--;
		}
		var target = entity.getTarget();
		if (target == null) return;
		if (!EnderTeleportModifier.mayTeleport(entity)) {
			return;
		}
		if (coolDown <= 0) {
			if (EnderTeleportModifier.teleportTowards(entity, target)) {
				EnderTeleportModifier.resetCooldown(entity);
				coolDown = SUCCESS_CD;
			} else {
				coolDown = FAIL_CD;
			}
		}
	}
}
