package dev.xkmc.modulargolems.compat.materials.twilightforest.modifier;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.ModConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import twilightforest.init.TFDimensionSettings;

import java.util.List;

public class TFHealingModifier extends GolemModifier {

	public TFHealingModifier() {
		super(StatFilterType.HEALTH, MAX_LEVEL);
	}

	@Override
	public double onHealTick(double heal, AbstractGolemEntity<?, ?> entity, int level) {
		if (entity.getLevel().dimensionType() == TFDimensionSettings.TWILIGHT_DIM_TYPE.get()) {
			return heal * (1 + ModConfig.COMMON.compatTFHealing.get() * level);
		}
		return heal;
	}

	public List<MutableComponent> getDetail(int v) {
		int bonus = (int) Math.round((1 + ModConfig.COMMON.compatTFHealing.get() * v) * 100);
		return List.of(Component.translatable(getDescriptionId() + ".desc", bonus).withStyle(ChatFormatting.GREEN));
	}

}
