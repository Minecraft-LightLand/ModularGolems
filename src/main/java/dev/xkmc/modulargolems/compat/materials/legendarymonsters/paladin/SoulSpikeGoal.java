package dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin;

import dev.xkmc.modulargolems.compat.materials.goety.multi.MultiTargetRangedGoal;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;

/**
 * Single-target trigger: soul spikes (SoulPillarEntity) summoned in front of the golem.
 * Mirrors PossessedPaladinEntity::spawnSoulPillar which summons a forward line of pillars.
 */
public class SoulSpikeGoal extends MultiTargetRangedGoal {

	public SoulSpikeGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(200, 0, 16, golem, lv);// 二倍原Boss冷却
	}

	@Override
	protected int searchRange() {
		return 16;
	}

	@Override
	protected int getMaxTarget() {
		return 1;
	}

	@Override
	protected int cd() {
		return 20;
	}

	@Override
	protected void performAttackImpl(LivingEntity target) {
		LMProxy.spawnSoulSpikes(golem, target, lv);
	}

}
