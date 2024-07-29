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

import java.util.List;

public class ExplosionResistanceModifier extends GolemModifier {

	public ExplosionResistanceModifier() {
		super(StatFilterType.HEALTH, 5);
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, DamageData.Defence event, int level) {
		float factor = (float) Math.max(0, 1 - level * MGConfig.COMMON.explosionResistance.get());
		if (!event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY) && event.getSource().is(DamageTypeTags.IS_EXPLOSION)) {
			event.addDealtModifier(DamageModifier.multTotal(factor, getRegistryName()));
		}
	}

	@Override
	public List<MutableComponent> getDetail(int level) {
		float factor = (float) Math.max(0, 1 - level * MGConfig.COMMON.explosionResistance.get());
		int perc = Math.round(100 * factor);
		return List.of(Component.translatable(getDescriptionId() + ".desc", perc).withStyle(ChatFormatting.GREEN));
	}


}
