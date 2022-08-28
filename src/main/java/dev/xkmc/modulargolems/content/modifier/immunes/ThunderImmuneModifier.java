package dev.xkmc.modulargolems.content.modifier.immunes;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.GolemModifier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class ThunderImmuneModifier extends GolemModifier {

	public ThunderImmuneModifier() {
		super(StatFilterType.HEALTH, 1);
	}

	@Override
	public void onAttacked(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		if (level > 0 && event.getSource() == DamageSource.LIGHTNING_BOLT) {
			event.setCanceled(true);
		}
	}

}
