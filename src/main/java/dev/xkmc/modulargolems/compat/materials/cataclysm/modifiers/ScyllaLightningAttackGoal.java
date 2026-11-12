package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.MWCataProxy;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BurstRangedGoal;
import net.minecraft.world.entity.LivingEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;

public class ScyllaLightningAttackGoal extends BurstRangedGoal {

	public ScyllaLightningAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(ScyllaLightningModifier.cooldownFor(lv), ScyllaLightningModifier.MIN_RANGE, ScyllaLightningModifier.MAX_RANGE, golem, lv);
	}

	@Override
	protected void fire(LivingEntity target, float lateral) {
		MWCataProxy.astrape(golem.level(), golem, target, lateral);
	}

	/** the first bolt leads and the rest follow together */
	@Override
	protected int getBurstDelay() {
		return 1;
	}

	/**
	 * 发射数量随生命值变化：满血 1 发，2/3 以下 3 发。风暴胸甲直接把傀儡钉在最终形态。
	 */
	@Override
	protected int getBurstCount() {
		// 满血 1 发；2/3 生命值以下 3 发。风暴胸甲把傀儡钉在完整形态，等于永久 3 发。
		return ScyllaLightningModifier.burstCount(golem);
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
