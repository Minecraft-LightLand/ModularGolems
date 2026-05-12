package dev.xkmc.modulargolems.content.modifier.common;

import dev.xkmc.l2damagetracker.contents.attack.CreateSourceEvent;
import dev.xkmc.l2damagetracker.contents.attack.OnDamageSourceModifyEvent;
import dev.xkmc.l2damagetracker.contents.damage.DefaultDamageState;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.DamageTypeTags;

import java.util.List;

public class AttackBypassArmorModifier extends GolemModifier {

	public AttackBypassArmorModifier(int max) {
		super(StatFilterType.ATTACK, max);
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		int perc = Math.round(MGConfig.COMMON.armorBypassChance.get().floatValue() * v * 100);
		return List.of(Component.translatable(getDescriptionId() + ".desc", perc).withStyle(ChatFormatting.GREEN));
	}

	@Override
	public void modifySource(AbstractGolemEntity<?, ?> golem, OnDamageSourceModifyEvent event, int value) {
		if (MGConfig.COMMON.armorBypassChance.get() * value > golem.getRandom().nextDouble())
			event.enable(DamageTypeTags.BYPASSES_ARMOR);
	}

}
