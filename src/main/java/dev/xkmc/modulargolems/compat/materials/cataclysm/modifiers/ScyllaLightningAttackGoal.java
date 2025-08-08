package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.mob_weapon_api.integration.cataclysm.CataclysmProxy;
import dev.xkmc.modulargolems.compat.materials.goety.multi.MultiTargetRangedGoal;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;

public class ScyllaLightningAttackGoal extends MultiTargetRangedGoal {

	public ScyllaLightningAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(100, 4, 35, golem, lv);
	}

	@Override
	protected int searchRange() {
		return 24 + lv * 4;
	}

	@Override
	protected int getMaxTarget() {
		return lv * 3;
	}

	@Override
	protected int cd() {
		return 20;
	}

	@Override
	protected void performAttackImpl(LivingEntity target) {
		CataclysmProxy.astrape(golem.level(), golem, target);
	}

}
