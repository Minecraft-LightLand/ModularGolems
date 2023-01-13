package dev.xkmc.modulargolems.compat.materials.create.modifier;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.GolemModifier;
import dev.xkmc.modulargolems.init.data.ModConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.List;

public class CoatingModifier extends GolemModifier {

	public CoatingModifier() {
		super(StatFilterType.MASS, 5);
	}

	public List<MutableComponent> getDetail(int v) {
		double reduce = v * ModConfig.COMMON.coating.get();
		return List.of(Component.translatable(getDescriptionId() + ".desc", reduce).withStyle(ChatFormatting.GREEN));
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, LivingDamageEvent event, int level) {
		event.setAmount((float) Math.max(0, event.getAmount() - level * ModConfig.COMMON.coating.get()));
	}

}
