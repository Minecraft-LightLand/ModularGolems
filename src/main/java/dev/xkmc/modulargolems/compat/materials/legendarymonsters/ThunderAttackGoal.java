package dev.xkmc.modulargolems.compat.materials.legendarymonsters;

import dev.xkmc.modulargolems.compat.materials.goety.multi.MultiTargetRangedGoal;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;

public class ThunderAttackGoal extends MultiTargetRangedGoal {

	public ThunderAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(100, 4, 35, golem, lv);
	}

	@Override
	protected int searchRange() {
		return 8 + lv * 8;
	}

	@Override
	protected int getMaxTarget() {
		return lv * 2;
	}

	@Override
	protected int cd() {
		return 20;
	}

	@Override
	protected void performAttackImpl(LivingEntity target) {
		LMProxy.performThunderAttack(golem, target, lv);
	}

}
