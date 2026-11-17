package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.init.data.MGLangData;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

public class ScyllaLineLightningModifier extends GolemModifier {
	public static final int COOLDOWN = 200;
	/** 施放距离上限（格）。 */
	public static final int MAX_RANGE = 30;
	/** 施放的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;

	/** 冷却随等级减半（每级右移 1 位，最低 1 tick）。等级效果只做冷却。 */
	public static int cooldownFor(int lv) {
		return Math.max(1, COOLDOWN >> (lv - 1));
	}

	/**
	 * 一道雷。本体 attackState 18 在 tick 25 与 53 各放一排，{@code k = -4..4} 共 9 道，
	 * 间距 1.25 格，沿**目标自身朝向**铺开（不是垂直于连线）。
	 */
	public static final int COUNT = 9;
	/** 相邻两道雷的间距（格），本体 {@code 1.25D * k}。 */
	public static final double SPACING = 1.25;
	/** 提前量：按目标位移外推多少 tick 后的落点，本体用 lifeticks = 13 / 20。 */
	public static final int LEAD = 13;
	/** 单道雷的体型（本体 {@code spawnLightning(..., 3.0F)}）。 */
	public static final float SIZE = 3.0F;
	/**
	 * 固定两排落雷的相对 tick（本体 tick 25 / 53，间隔 28）。{@code performAttack} 在 tick 末尾才被调用、
	 * {@code castTicks} 从 1 开始，所以第一排设在 1、第二排 1 + 28 = 29。
	 */
	public static final int FIRST_TICK = 1;
	public static final int SECOND_TICK = 29;
	/** 本体 tick 24~54 在脚下持续冒圈状闪电粒子。 */
	public static final int SPARK_START = 24;
	public static final int SPARK_END = 54;

	public ScyllaLineLightningModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new ScyllaLineLightningAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

		@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(
				Component.translatable(getDescriptionId() + ".desc").withStyle(ChatFormatting.GREEN),
				MGLangData.SKILL_HEALTH_GATE.get(
						CataArmorHelper.healthPct(CataArmorHelper.TWO_THIRDS))
						.withStyle(ChatFormatting.GREEN));
	}

}
