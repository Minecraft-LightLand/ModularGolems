package dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * 翻滚斩（对应堕落圣骑 Boss state29）：向前翻滚冲刺，沿途留下灵魂柱，
 * 并在冲刺中打出两次 360° 斩。做成真冲锋技能（{@link ChargeHelper.Modifier} 体系）。
 */
public class PaladinRollSlashModifier extends GolemModifier implements ChargeHelper.Modifier {

	public static final int COOLDOWN = 200;
	/** 冲锋距离上限（格）。 */
	public static final int MAX_RANGE = 16;
	/** 冲锋的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;

	public PaladinRollSlashModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public int getCooldown(AbstractGolemEntity<?, ?> golem, int lv) {
		return COOLDOWN;
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		// priority 1：高于近战 Goal，冲锋才能抢到 MOVE/LOOK 控制权
		addGoal.accept(1, new PaladinRollSlashAttackGoal(entity, lv, this));
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc", ChargeHelper.chargeReduction()).withStyle(ChatFormatting.GREEN));
	}

}
