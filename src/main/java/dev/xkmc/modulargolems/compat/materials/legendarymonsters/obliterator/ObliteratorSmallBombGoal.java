package dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator;

import dev.xkmc.modulargolems.compat.materials.goety.multi.MultiTargetRangedGoal;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;

/**
 * Multi-target ranged: small bomb (SmallAnnihilationBombEntity)
 * Original: TheObliteratorEntity:4950 shootAngledBombs / state 7 double, spawned per target
 * Multi proxy: one SmallAnnihilationBomb per target, multiple per attack move.
 */
public class ObliteratorSmallBombGoal extends MultiTargetRangedGoal {

	public ObliteratorSmallBombGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(100, 0, 35, golem, lv);
	}

	@Override
	protected int searchRange() {
		return 24;
	}

	@Override
	protected int getMaxTarget() {
		return 2 + lv;
	}

	@Override
	protected int cd() {
		return 8;
	}

	@Override
	protected void performAttackImpl(LivingEntity target) {
		LMProxy.spawnObliteratorSmallBomb(golem, target, lv);
	}
}
