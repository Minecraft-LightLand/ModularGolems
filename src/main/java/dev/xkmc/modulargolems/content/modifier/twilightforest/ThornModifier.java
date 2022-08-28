package dev.xkmc.modulargolems.content.modifier.twilightforest;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.GolemModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.List;

public class ThornModifier extends GolemModifier {

	public static final double PERCENTAGE = 0.2; //TODO move to config

	public ThornModifier() {
		super(StatFilterType.HEALTH, MAX_LEVEL);
	}

	@Override
	public void onAttacked(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		if (level == 0) {
			return;
		}
		DamageSource source = event.getSource();
		if (source.isMagic() || source instanceof EntityDamageSource eds && eds.isThorns()) {
			return;
		}
		if (source.getDirectEntity() instanceof LivingEntity living && living.isAlive()) {
			living.hurt(DamageSource.thorns(entity), (float) (event.getAmount() * PERCENTAGE * level));
		}
	}

	public List<MutableComponent> getDetail(int v) {
		int reflect = (int) Math.round(PERCENTAGE * v * 100);
		return List.of(Component.translatable(getDescriptionId() + ".desc", reflect).withStyle(ChatFormatting.GREEN));
	}

}
