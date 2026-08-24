package dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.LivingEntity;

/**
 * Single-target ranged: large bomb (AnnihilationBombEntity)
 * Original: TheObliteratorEntity:4935 shootAnnihilationBomb / state 6 single, 55 single
 * Proxy spawns only once per attack move.
 */
public class ObliteratorLargeBombGoal extends BaseRangedAttackGoal {

	public ObliteratorLargeBombGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(80, 0, 24, golem, lv);
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		if (golem.level().isClientSide) return true;
		LMProxy.spawnObliteratorLargeBomb(golem, target, lv);
		return true;
	}
}
