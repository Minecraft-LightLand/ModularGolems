package dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

/**
 * Multi-target small bomb modifier (Obliterator).
 * Each target receives one SmallAnnihilationBombEntity, attack hits multiple targets.
 * Reference: TheObliteratorEntity state 7 double bomb, shootAngledBombs with bombCount>1
 */
public class ObliteratorSmallBombModifier extends GolemModifier {

	/** 冷却（tick）。 */
	public static final int COOLDOWN = 160;
	/** 施放距离上限（格）。 */
	public static final int MAX_RANGE = 35;
	/** 施放的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;
	/** 一次齐射的炸弹数量。 */
	public static final int BURST_COUNT = 3;
	/** 齐射中两发之间的间隔（tick）：1+1+1 节奏，即每发单独放出。 */
	public static final int BURST_DELAY = 5;

	/** 每级减半冷却（参考灾变鸣雷槊）。 */
	public static int cooldownFor(int lv) {
		return Math.max(1, COOLDOWN >> (lv - 1));
	}

	public ObliteratorSmallBombModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new ObliteratorSmallBombAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}
//  目前用不到，留作备份
//	@Override
//	public boolean canExistOn(GolemPart<?, ?> part) {
//		return part.getEntityType() != GolemTypes.TYPE_GOLEM.get() && super.canExistOn(part);
//	}

}
