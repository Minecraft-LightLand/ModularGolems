package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.compat.materials.cataclysm.CataclysmProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.LivingEntity;

public class AncientRemnantSandstormAttackGoal extends BaseRangedAttackGoal {

	public AncientRemnantSandstormAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(100, 4, 35, golem, lv);
	}

	@Override
	protected void performAttack(LivingEntity target) {
		CataclysmProxy.sandstormAttack(golem, target, 100);
	}

}
