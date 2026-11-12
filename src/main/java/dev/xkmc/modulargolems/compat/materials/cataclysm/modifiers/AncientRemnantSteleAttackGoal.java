package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.LivingEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;

public class AncientRemnantSteleAttackGoal extends BaseRangedAttackGoal {


	public AncientRemnantSteleAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(AncientRemnantSteleModifier.COOLDOWN, AncientRemnantSteleModifier.MIN_RANGE, AncientRemnantSteleModifier.MAX_RANGE, golem, lv);
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		AncientRemnantSteleModifier.addStele(golem, target, lv);
		return true;
	}

	/**
	 * 远古金属胸甲：半血以下（或被胸甲钉在最终形态）时冷却减半。
	 */
	@Override
	protected int getWaitTime() {
		return CataArmorHelper.ancientFastSkill(golem) ? AncientRemnantSteleModifier.COOLDOWN / 2 : AncientRemnantSteleModifier.COOLDOWN;
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
