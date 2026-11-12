package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

public class EnderGuardianHugModifier extends GolemModifier {

	/**
	 * 冷却基础值；等级每升一级右移一位（减半），永远不会减到 0。
	 * 与咒魂箭 {@code MaledictusArrowModifier.COOLDOWN >> (lv - 1)} 同一套做法。
	 */
	public static final int COOLDOWN = 300;

	/** 冷却随等级右移（每级减半），最低 1 tick，永不归零。 */
	public static int cooldownFor(int lv) {
		return Math.max(1, COOLDOWN >> (lv - 1));
	}

	public static final int MAX_TICKS = 30;
	public static final double MAX_RANGE = 32;

	public EnderGuardianHugModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(1, new EnderGuardianHugAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

}
