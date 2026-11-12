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
	public static final int COOLDOWN = 100;
	/** 施放距离上限（格）。 */
	public static final int MAX_RANGE = 30;
	/** 施放的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;

	public static final int COUNT = 5;
	public static final int SPACING = 2;
	public static final int LEAD = 10;

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
