package dev.xkmc.modulargolems.content.modifier.immunes;

import dev.xkmc.modulargolems.content.core.GolemModifier;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class FireImmuneModifier extends GolemModifier {

	public FireImmuneModifier() {
		super(1);
	}

	@Override
	public void onAttacked(AbstractGolemEntity<?> entity, LivingAttackEvent event, int level) {
		if (level > 0 && event.getSource().isFire()) {
			event.setCanceled(true);
		}
	}

}
