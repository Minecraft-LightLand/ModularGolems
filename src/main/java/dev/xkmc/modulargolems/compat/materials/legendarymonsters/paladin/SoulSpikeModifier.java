package dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

/**
 * 堕落圣骑傀儡手臂：在前方召唤灵魂尖刺（SoulPillarEntity）
 * 使用PossessedPaladinEntity::spawnSoulPillar; at level 2 the spike count matches the boss volley (5).
 * The pillar heals the caster automatically on hit (handled inside SoulPillarEntity).
 */
public class SoulSpikeModifier extends GolemModifier {

	public SoulSpikeModifier() {
		super(StatFilterType.ATTACK, 2);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new SoulSpikeGoal(entity, lv));
	}

}
