package dev.xkmc.modulargolems.compat.materials.create.modifier;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.List;

public class CoatingModifier extends GolemModifier {

	public CoatingModifier() {
		super(StatFilterType.MASS, 5);
	}

	public List<MutableComponent> getDetail(int v) {
		double reduce = v * MGConfig.COMMON.coating.get();
		// coating is a DoubleValue, so the raw double would render a bare "1.0"; the attribute format
		// drops trailing zeros while keeping real fractions like "0.5"
		var val = Component.literal(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(reduce)).withStyle(ChatFormatting.AQUA);
		return List.of(Component.translatable(getDescriptionId() + ".desc", val).withStyle(ChatFormatting.GREEN));
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, LivingDamageEvent event, int level) {
		event.setAmount((float) Math.max(0, event.getAmount() - level * MGConfig.COMMON.coating.get()));
	}

}
