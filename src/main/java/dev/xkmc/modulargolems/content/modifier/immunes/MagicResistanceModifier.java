package dev.xkmc.modulargolems.content.modifier.immunes;

import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.DamageTypeTags;
import net.neoforged.neoforge.common.Tags;

import java.util.List;

public class MagicResistanceModifier extends GolemModifier {

	public MagicResistanceModifier() {
		super(StatFilterType.HEALTH, 5);
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, DamageData.Defence event, int level) {
		float factor = (float) Math.max(0, 1 - level * MGConfig.COMMON.magicResistance.get());
		if (!event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY) && event.getSource().is(Tags.DamageTypes.IS_MAGIC)) {
			event.addDealtModifier(DamageModifier.multTotal(factor, getRegistryName()));
		}
	}

	@Override
	public List<MutableComponent> getDetail(int level) {
		float factor = (float) Math.max(0, 1 - level * MGConfig.COMMON.magicResistance.get());
		int perc = Math.round(100 * factor);
		return List.of(Component.translatable(getDescriptionId() + ".desc", perc).withStyle(ChatFormatting.GREEN));
	}

}
