package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * 扫戟扇形阵：向前方扇形区域召唤大量幻影战戟。仅生命值低于一半时可用。
 */
public class MaledictusHalberdFanModifier extends GolemModifier {
	public static final int COOLDOWN = 200;
	/** 施放距离上限（格）。 */
	public static final int MAX_RANGE = 18;
	/** 施放的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;

	/** 本体的扇形张角，单位 π：0.7 → 126°。 */
	public static final float SPREAD_ARC = 0.7F;
	/** 本体的逐圈半径：2, 4, 6, … 16。 */
	public static final int START_DISTANCE = 2;
	public static final int END_DISTANCE = 16;
	public static final int STEP = 2;
	public static final int RING_COUNT = (END_DISTANCE - START_DISTANCE) / STEP + 1;

	public MaledictusHalberdFanModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new MaledictusHalberdFanAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(
				Component.translatable(getDescriptionId() + ".desc").withStyle(ChatFormatting.GREEN),
				MGLangData.SKILL_HEALTH_GATE.get(
						CataArmorHelper.healthPct(CataArmorHelper.HALF_HEALTH))
						.withStyle(ChatFormatting.GREEN));
	}

}
