package dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor.LMArmorHelper;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * 堕魂掷戟（对应堕落圣骑 Boss state38）：投出灵魂三叉戟，落地产生大量灵魂爆破。
 * 仅二阶段（生命 &lt;65%）解锁。Boss 原版后续的两次 90° 斩未复刻。
 */
public class PaladinSoulTridentModifier extends GolemModifier {

	public static final int COOLDOWN = 200;

	/** 每级减半冷却（参考灾变鸣雷槊）。 */
	public static int cooldownFor(int lv) {
		return Math.max(1, COOLDOWN >> (lv - 1));
	}
	public static final int MAX_RANGE = 24;
	public static final int MIN_RANGE = 0;

	public PaladinSoulTridentModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new PaladinSoulTridentAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(
				Component.translatable(getDescriptionId() + ".desc").withStyle(ChatFormatting.GREEN),
				LMArmorHelper.phaseTwoGate().withStyle(ChatFormatting.GREEN));
	}

}