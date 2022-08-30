package dev.xkmc.modulargolems.content.modifier.twilightforest;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.GolemModifier;
import dev.xkmc.modulargolems.init.data.ModConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;

public class FieryModifier extends GolemModifier {

	private static float getPercent() {
		return (float) (double) ModConfig.COMMON.fiery.get();
	}

	public FieryModifier() {
		super(StatFilterType.ATTACK, MAX_LEVEL);
	}

	public List<MutableComponent> getDetail(int v) {
		int reflect = Math.round((1 + getPercent() * v) * 100);
		return List.of(Component.translatable(getDescriptionId() + ".desc", reflect).withStyle(ChatFormatting.GREEN));
	}

	@Override
	public void onHurtTarget(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {
		if (!event.getEntity().fireImmune()) {
			event.getSource().setIsFire();
			event.getEntity().setSecondsOnFire(10);
			event.setAmount(event.getAmount() * (1 + getPercent() * level));
		}
	}
}
