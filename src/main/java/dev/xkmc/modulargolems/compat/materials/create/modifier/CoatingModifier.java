package dev.xkmc.modulargolems.compat.materials.create.modifier;

import net.minecraft.world.item.component.ItemAttributeModifiers;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class CoatingModifier extends GolemModifier {

	public CoatingModifier() {
		super(StatFilterType.MASS, 5);
	}

	public List<MutableComponent> getDetail(int v) {
		double reduce = v * MGConfig.COMMON.coating.get();
		// coating is a DoubleValue, so the raw double would render a bare "1.0"; the attribute format
		// drops trailing zeros while keeping real fractions like "0.5"
		var val = Component.literal(ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(reduce)).withStyle(ChatFormatting.AQUA);
		return List.of(Component.translatable(getDescriptionId() + ".desc", val).withStyle(ChatFormatting.GREEN));
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, DamageData.Defence event, int level) {
		event.addDealtModifier(DamageModifier.add((float) (-level * MGConfig.COMMON.coating.get()), getRegistryName()));
	}

}
