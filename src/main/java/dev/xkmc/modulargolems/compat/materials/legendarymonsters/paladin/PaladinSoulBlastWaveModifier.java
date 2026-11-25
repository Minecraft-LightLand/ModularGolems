package dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

/**
 * 堕落圣骑傀儡：向目标方向以扇形释放灵魂冲击波（SoulPillarEntity 灵魂柱）
 * 使用PossessedPaladinEntity::spawnSoulPillar; at level 2 the pillar count matches the boss volley (5).
 * The pillar heals the caster automatically on hit (handled inside SoulPillarEntity).
 */
public class PaladinSoulBlastWaveModifier extends GolemModifier {

	/** 冷却（tick）。 */
	public static final int COOLDOWN = 200;
	/** 施放距离上限（格），对应 Boss 冲击波最远一圈 16。 */
	public static final int MAX_RANGE = 16;
	/** 施放的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;

	public PaladinSoulBlastWaveModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new PaladinSoulBlastWaveAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

}
