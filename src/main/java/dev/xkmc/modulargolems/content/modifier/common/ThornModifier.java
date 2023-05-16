package dev.xkmc.modulargolems.content.modifier.common;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.GolemModifier;
import dev.xkmc.modulargolems.init.data.ModConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;

public class ThornModifier extends GolemModifier {

	private static float getPercent() {
		return (float) (double) ModConfig.COMMON.thorn.get();
	}

	public ThornModifier() {
		super(StatFilterType.HEALTH, MAX_LEVEL);
	}

	@Override
	public void onHurt(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {
		if (level == 0) {
			return;
		}
		DamageSource source = event.getSource();
		if (source.isMagic() || source instanceof EntityDamageSource eds && eds.isThorns()) {
			return;
		}
		if (source.getDirectEntity() instanceof LivingEntity living && living.isAlive()) {
			living.hurt(DamageSource.thorns(entity), event.getAmount() * getPercent() * level);
		}
	}

	public List<MutableComponent> getDetail(int v) {
		int reflect = Math.round(getPercent() * v * 100);
		return List.of(Component.translatable(getDescriptionId() + ".desc", reflect).withStyle(ChatFormatting.GREEN));
	}

}
