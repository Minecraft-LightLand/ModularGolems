package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

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

public class HarbingerHowitzerModifier extends GolemModifier {
	public static final int COOLDOWN = 200;
	/** 施放距离上限（格）。 */
	public static final int MAX_RANGE = 20;
	/** 施放的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;

	public HarbingerHowitzerModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(1, new HarbingerHowitzerAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

	@Override
	public java.util.List<MutableComponent> getDetail(int v) {
		return java.util.List.of(Component.translatable(getDescriptionId() + ".desc",
				CataArmorHelper.healthPct(CataArmorHelper.HALF_HEALTH)).withStyle(ChatFormatting.GREEN));
	}

}
