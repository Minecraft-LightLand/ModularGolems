package dev.xkmc.modulargolems.compat.materials.twilightforest.modifier;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.GolemModifier;
import dev.xkmc.modulargolems.init.data.ModConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.List;

public class CarminiteModifier extends GolemModifier {

	public CarminiteModifier() {
		super(StatFilterType.MASS, 5);
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, LivingDamageEvent event, int level) {
		int time = ModConfig.COMMON.carminiteTime.get() * level;
		entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, time, 4), entity);
		entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, time), entity);
	}

	public List<MutableComponent> getDetail(int level) {
		double time = ModConfig.COMMON.carminiteTime.get() * level / 20d;
		return List.of(Component.translatable(getDescriptionId() + ".desc", time).withStyle(ChatFormatting.GREEN));
	}

}
