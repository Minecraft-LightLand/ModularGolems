package dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin;

import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

/**
 * 堕魂之刃（参考腾炎傀儡的焰魔之刃 {@code IgnisAttackModifier}）：
 * 对攻击的目标叠加碎魂与碎甲效果。生命值低于 65%（二阶段）时额外附带流血。
 * <p>
 * Boss 侧碎魂来自 {@code PossessedPaladinEntity} 的 4 处 {@code applyStackingEffect(... SOUL_FRACTURE, 1, 4, 200)}，
 * 碎甲来自 {@code NewPossessedPaladin#BreakArmor}。
 */
public class PaladinSoulBladeModifier extends GolemModifier {

	/** 二阶段阈值（Boss 的 Crackiness.MEDIUM）。 */
	public static final float PHASE_TWO = 0.65F;

	public PaladinSoulBladeModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		var threshold = Component.literal(Math.round(PHASE_TWO * 100) + "%")
				.withStyle(ChatFormatting.AQUA);
		return List.of(Component.translatable(getDescriptionId() + ".desc", threshold).withStyle(ChatFormatting.GREEN));
	}

	@Override
	public void finalizeHurtTarget(AttackCache cache, AbstractGolemEntity<?, ?> golem, int level) {
		LivingEntity target = cache.getAttackTarget();
		if (target == null) return;
		LMProxy.paladinSoulBladeHit(golem, target, level);
	}

}
