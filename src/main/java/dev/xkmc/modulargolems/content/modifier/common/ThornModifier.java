package dev.xkmc.modulargolems.content.modifier.common;

import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.init.data.L2DamageTypes;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class ThornModifier extends GolemModifier {

	private static float getPercent() {
		return (float) (double) MGConfig.COMMON.thorn.get();
	}

	public ThornModifier() {
		super(StatFilterType.HEALTH, MAX_LEVEL);
	}

	@Override
	public void onDamageMax(AttackCache cache, AbstractGolemEntity<?, ?> golem, int level) {
		if (level <= 0) return;
		var event = cache.getLivingHurtEvent();
		assert event != null;
		DamageSource source = event.getSource();
		if (source.is(L2DamageTypes.MAGIC) || source.is(DamageTypes.THORNS) || source.is(DamageTypeTags.AVOIDS_GUARDIAN_THORNS)) {
			return;
		}
		if (source.getDirectEntity() instanceof LivingEntity living && living.isAlive()) {
			living.hurt(golem.level().damageSources().thorns(golem), event.getAmount() * getPercent() * level);
		}
	}

	public List<MutableComponent> getDetail(int v) {
		int reflect = Math.round(getPercent() * v * 100);
		return List.of(Component.translatable(getDescriptionId() + ".desc", reflect).withStyle(ChatFormatting.GREEN));
	}

}
