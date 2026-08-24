package dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator;

import dev.xkmc.modulargolems.compat.materials.goety.multi.MultiTargetRangedGoal;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;

/**
 * Multi-target ranged: plasma orb (PlasmaOrbEntity)
 * Original: TheObliteratorEntity:5084 shootPlasmaBall / state 22 jump teleport with 3 calls (2+2+1)
 * Multi proxy: one PlasmaOrbEntity per target.
 */
public class ObliteratorPlasmaOrbGoal extends MultiTargetRangedGoal {

	public ObliteratorPlasmaOrbGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(120, 0, 24, golem, lv);
	}

	@Override
	protected int searchRange() {
		return 20;
	}

	@Override
	protected int getMaxTarget() {
		return 1 + lv;
	}

	@Override
	protected int cd() {
		return 12;
	}

	@Override
	protected void performAttackImpl(LivingEntity target) {
		LMProxy.spawnObliteratorPlasmaOrb(golem, target, lv);
	}
}
