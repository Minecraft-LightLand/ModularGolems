package dev.xkmc.modulargolems.compat.materials.twilightforest;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import twilightforest.init.TFDimensionSettings;

import java.util.List;

public class TFHealingModifier extends GolemModifier {

	public TFHealingModifier() {
		super(StatFilterType.HEALTH, MAX_LEVEL);
	}

	@Override
	public double onInventoryHealTick(double heal, Entity entity, int level) {
		if (entity.level().dimensionTypeId().equals(TFDimensionSettings.TWILIGHT_DIM_TYPE)) {
			return heal * (1 + MGConfig.COMMON.compatTFHealing.get() * level);
		}
		return heal;
	}

	public List<MutableComponent> getDetail(int v) {
		int bonus = (int) Math.round((1 + MGConfig.COMMON.compatTFHealing.get() * v) * 100);
		return List.of(Component.translatable(getDescriptionId() + ".desc", bonus).withStyle(ChatFormatting.GREEN));
	}

}
