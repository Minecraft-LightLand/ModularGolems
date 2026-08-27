package dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
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

//	@Override
//	public boolean canExistOn(GolemPart<?, ?> part) {
//		return part == GolemItems.GOLEM_ARM.get() || part == GolemItems.HUMANOID_ARMS.get();
//	}
}
