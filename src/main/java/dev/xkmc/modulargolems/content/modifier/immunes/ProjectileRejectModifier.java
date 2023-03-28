package dev.xkmc.modulargolems.content.modifier.immunes;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.GolemModifier;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class ProjectileRejectModifier extends GolemModifier {

	public ProjectileRejectModifier() {
		super(StatFilterType.HEALTH, 1);
	}

	@Override
	public void onAttacked(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		if (level > 0 && !event.getSource().isBypassInvul() && event.getSource().isProjectile()) {
			event.setCanceled(true);
		}
	}

}
