package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BurstRangedGoal;
import net.minecraft.world.entity.LivingEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;

public class ScyllaWaterBoltGoal extends BurstRangedGoal {

	public ScyllaWaterBoltGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(0, 35, golem, lv);
	}

	@Override
	protected void fire(LivingEntity target, float lateral) {
		GolemCataProxy.scyllaSpearThrow(golem, target, false, lateral);
	}

	/** the first bolt leads and the rest follow together */
	@Override
	protected int getBurstDelay() {
		return 1;
	}

	/**
	 * 发射数量随生命值变化：满血 1 发，2/3 以下 3 发。
	 */
	@Override
	protected int getBurstCount() {
		float ratio = golem.getGuardedDataImpl() / golem.getMaxHealth();
		return ratio <= 2.0F / 3 ? 3 : 1;
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
