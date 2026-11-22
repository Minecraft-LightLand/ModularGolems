package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.MWCataProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;
import dev.xkmc.modulargolems.content.modifier.special.BurstRangedGoal;
import net.minecraft.world.entity.LivingEntity;

public class ScyllaLightningAttackGoal extends BurstRangedGoal {

	public ScyllaLightningAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(0, 35, golem, lv);
	}

	@Override
	protected void fire(LivingEntity target, float lateral) {
		MWCataProxy.astrape(golem.level(), golem, target, lateral);
	}

	@Override
	protected int getBurstDelay() {
		return 1;
	}

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
