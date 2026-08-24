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

	private net.minecraft.world.entity.Entity beam;

	public ObliteratorLaserGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(100, 2, 32, golem, lv);
	}

	@Override
	public boolean canContinueToUse() {
		if (beam != null) return true;
		return super.canContinueToUse();
	}

	@Override
	public void tick() {
		if (beam != null) {
			if (beam.isRemoved()) {
				beam = null;
			} else {
				var target = golem.getTarget();
				if (target != null) {
					golem.getLookControl().setLookAt(target, 30, 90);
				}
				// AnnihilationBeamEntity follows caster rotation via updateWithCaster() (AnnihilationBeamEntity:217 updateWithCaster reads caster.f_20885 / m_146909 and repositions beam each tick)
				// No explicit setPosRaw needed - internal sync handles forward offset (2 blocks) and y+2. Mirrors AnnihilatorLaserAttackGoal tick look control.
			}
		}
		super.tick();
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		if (golem.level().isClientSide) return true;
		net.minecraft.world.entity.Entity laser = LMProxy.spawnObliteratorLaser(golem, target, lv);
		if (laser == null) return true;
		beam = laser;
		golem.getNavigation().stop();
		golem.specialAttackCoolDown = 20;
		return true;
	}
}
