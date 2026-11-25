package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import java.util.List;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
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

	/** 冷却随等级减半（每级右移 1 位，最低 1 tick）。 */
	public static int cooldownFor(int lv) {
		return Math.max(1, COOLDOWN >> (lv - 1));
	}

	public static final int COUNT = 9;
	public static final double SPACING = 1.25;
	public static final int LEAD = 13;
	public static final float SIZE = 3.0F;
	public static final int FIRST_TICK = 1;
	public static final int SECOND_TICK = 29;
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
