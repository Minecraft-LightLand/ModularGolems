package dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin;

import dev.xkmc.modulargolems.compat.materials.goety.multi.MultiTargetRangedGoal;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;

/**
 * Single-target ranged: phantom dagger (ThrownPhantomDaggerEntity).
 * Mirrors PosessedPaladinEntity::throwPhantomDaggers: three homing daggers toward one target.
 */
public class PhantomDaggerGoal extends MultiTargetRangedGoal {

	public PhantomDaggerGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(100, 0, 30, golem, lv);
	}

	@Override
	protected int searchRange() {
		return 24;
	}

	@Override
	protected int getMaxTarget() {
		return 1;
	}

	@Override
	protected int cd() {
		return 15;
	}

	@Override
	protected void performAttackImpl(LivingEntity target) {
		LMProxy.spawnPhantomDaggers(golem, target, lv);
	}

}
