package dev.xkmc.modulargolems.compat.materials.eeeab.realm;

import dev.xkmc.modulargolems.content.modifier.special.BurstRangedGoal;
import dev.xkmc.modulargolems.compat.materials.eeeab.EEEABProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;

/**
 * 界域戍卫后跳齐射导弹的 Goal，见 {@link AnnihilationMissileModifier}。
 * 参考 {@code EntityRealmWarden.spawnAnnihilatorMissiles}。
 */
public class AnnihilationMissileAttackGoal extends BurstRangedGoal {

	public AnnihilationMissileAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(AnnihilationMissileModifier.COOLDOWN, AnnihilationMissileModifier.MIN_RANGE, AnnihilationMissileModifier.MAX_RANGE, golem, lv);
	}

	@Override
	protected double searchRange() {
		return AnnihilationMissileModifier.MAX_RANGE;
	}

	@Override
	protected int getBurstCount() {
		return 1;
	}

	@Override
	protected int getBurstDelay() {
		return AnnihilationMissileModifier.BURST_DELAY;
	}

	@Override
	protected int getWaitTime() {
		return AnnihilationMissileModifier.cooldownFor(lv);
	}

	protected void fire(LivingEntity target, float lateral) {
		if (golem.level().isClientSide) return;
		EEEABProxy.shootAnnihilatorMissile(golem, target);
	}

	@Override
	protected BackstepHelper backstep() {
		return new BackstepHelper(golem, BackstepHelper.DEFAULT_DISTANCE, BackstepHelper.DEFAULT_HEIGHT);
	}


	@Override
	protected double getLeapRange() {
		return BackstepHelper.DEFAULT_LEAP_RANGE;
	}
}
