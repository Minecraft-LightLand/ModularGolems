package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.LivingEntity;

public class LeviathanBlastPortalAttackGoal extends BaseRangedAttackGoal {

	public LeviathanBlastPortalAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(100, 2, 35, golem, lv);
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		LeviathanBlastPortalModifier.addBeam(golem, target);
		return true;
	}

}

