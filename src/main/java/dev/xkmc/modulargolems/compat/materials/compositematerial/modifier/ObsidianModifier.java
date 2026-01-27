package dev.xkmc.modulargolems.compat.materials.compositematerial.modifier;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.List;

public class ObsidianModifier extends GolemModifier {

	public ObsidianModifier() {
		super(StatFilterType.MASS, 5);
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, LivingDamageEvent event, int level) {
		int factor = MGConfig.COMMON.obsidianDRFactor.get();
		event.setAmount((float) Math.max(0, event.getAmount() - level * factor));//TODO config
	}

	public List<MutableComponent> getDetail(int v) {
		int factor = v * MGConfig.COMMON.obsidianDRFactor.get();
		return List.of(Component.translatable(getDescriptionId() + ".desc", factor).withStyle(ChatFormatting.GREEN));
	}
}
