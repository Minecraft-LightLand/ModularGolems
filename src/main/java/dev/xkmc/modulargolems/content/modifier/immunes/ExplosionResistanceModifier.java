package dev.xkmc.modulargolems.content.modifier.immunes;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.ModConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;

public class ExplosionResistanceModifier extends GolemModifier {

	public ExplosionResistanceModifier() {
		super(StatFilterType.HEALTH, 5);
	}

	@Override
	public void onHurt(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {
		float factor = (float) Math.max(0, 1 - level * ModConfig.COMMON.explosionResistance.get());
		if (!event.getSource().isBypassInvul() && event.getSource().isExplosion()) {
			event.setAmount(event.getAmount() * factor);
		}
	}

	@Override
	public List<MutableComponent> getDetail(int level) {
		float factor = (float) Math.max(0, 1 - level * ModConfig.COMMON.explosionResistance.get());
		int perc = Math.round(100 * factor);
		return List.of(Component.translatable(getDescriptionId() + ".desc", perc).withStyle(ChatFormatting.GREEN));
	}


}
