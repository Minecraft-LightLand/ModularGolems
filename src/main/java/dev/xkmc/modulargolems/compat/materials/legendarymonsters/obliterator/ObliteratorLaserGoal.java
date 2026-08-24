package dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.LivingEntity;

/**
 * Single-target ranged: laser (AnnihilationBeamEntity)
 * Original: TheObliteratorEntity:3743 singleShotLaser states 21/42/43 single beam (uniformDuration 10, damage 8.5)
 * Single proxy: one AnnihilationBeamEntity per attack move.
 * Quad beam (46) is multi-target variant (4 beams), not used here.
 */
public class ObliteratorLaserGoal extends BaseRangedAttackGoal {

	public ObliteratorLaserGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(100, 2, 32, golem, lv);
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		if (golem.level().isClientSide) return true;
		LMProxy.spawnObliteratorLaser(golem, target, lv);
		return true;
	}
}
