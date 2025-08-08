package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.mob_weapon_api.integration.cataclysm.CataclysmProxy;
import dev.xkmc.modulargolems.compat.materials.goety.multi.MultiTargetRangedGoal;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;

public class EnderGuardianVoidRuneAttackGoal extends MultiTargetRangedGoal {

	public EnderGuardianVoidRuneAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(100, 0, 15, golem, lv);
	}

	@Override
	protected int searchRange() {
		return 15;
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
		EnderGuardianVoidRuneModifier.addRune(golem, target, lv);
		CataclysmProxy.spawnVortex(golem, target.position());
	}

}

