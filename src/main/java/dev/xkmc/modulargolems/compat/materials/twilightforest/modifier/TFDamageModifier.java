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

public class TFDamageModifier extends GolemModifier {

	public TFDamageModifier() {
		super(StatFilterType.ATTACK, MAX_LEVEL);
	}

	@Override
	public float modifyDamage(float damage, AbstractGolemEntity<?, ?> entity, int level) {
		if (entity.getLevel().dimensionTypeId().equals(TFDimensionSettings.TWILIGHT_DIM_TYPE)) {
			return (float) (damage * (1 + ModConfig.COMMON.compatTFDamage.get() * level));
		}
		return damage;
	}

	public List<MutableComponent> getDetail(int v) {
		int bonus = (int) Math.round((1 + ModConfig.COMMON.compatTFDamage.get() * v) * 100);
		return List.of(Component.translatable(getDescriptionId() + ".desc", bonus).withStyle(ChatFormatting.GREEN));
	}

}
