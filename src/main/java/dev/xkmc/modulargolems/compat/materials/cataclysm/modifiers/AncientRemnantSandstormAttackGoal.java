package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.LivingEntity;

public class AncientRemnantSandstormAttackGoal extends BaseRangedAttackGoal {


	public AncientRemnantSandstormAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(AncientRemnantSandstormModifier.COOLDOWN, AncientRemnantSandstormModifier.MIN_RANGE, AncientRemnantSandstormModifier.MAX_RANGE, golem, lv);
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		AncientRemnantChargeModifier.addChargeReduction(golem);
		var group = AncientRemnantSandstormGroup.of(golem);
		group.maintain(golem, lv);
		group.assign(golem);
		return true;
	}

	/**
	 * 远古金属胸甲：半血以下（或被胸甲钉在最终形态）时冷却减半。
	 */
	@Override
	protected int getWaitTime() {
		return CataArmorHelper.ancientFastSkill(golem) ? AncientRemnantSandstormModifier.COOLDOWN / 2 : AncientRemnantSandstormModifier.COOLDOWN;
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
