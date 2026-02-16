package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.compat.materials.cataclysm.CataclysmProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.MultiTargetRangedGoal;
import net.minecraft.world.entity.LivingEntity;

public class AncientRemnantSandstormAttackGoal extends MultiTargetRangedGoal {

	public AncientRemnantSandstormAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(100, 0, 35, golem, lv);
	}

	@Override
	protected int searchRange() {
		return 35;
	}

	@Override
	protected int getMaxTarget() {
		return lv * 3;
	}

	@Override
	protected int cd() {
		return 10;
	}

	@Override
	protected void performAttackImpl(LivingEntity target) {
		CataclysmProxy.sandstormAttack(golem, target, 100);
	}

}
