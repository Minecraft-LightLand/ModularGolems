package dev.xkmc.modulargolems.compat.materials.cataclysm;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.LivingEntity;

public class HarbingerDeathBeamAttackGoal extends BaseRangedAttackGoal {

	public HarbingerDeathBeamAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(100, 4, 35, golem, lv);
	}

	@Override
	protected void performAttack(LivingEntity target) {
		HarbingerDeathBeamModifier.addBeam(golem);
		golem.getNavigation().stop();
	}

}

