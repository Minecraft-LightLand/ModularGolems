package dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin;

import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor.LMArmorHelper;
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

	public PaladinSoulBladeModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc", LMArmorHelper.phaseTwoPct())
				.withStyle(ChatFormatting.GREEN));
	}

	@Override
	public void onHurtTarget(AbstractGolemEntity<?, ?> golem, DamageData.Offence cache, int level) {
		LivingEntity target = cache.getTarget();
		if (target == null) return;
		LMProxy.paladinSoulBladeHit(golem, target, level);
	}

}