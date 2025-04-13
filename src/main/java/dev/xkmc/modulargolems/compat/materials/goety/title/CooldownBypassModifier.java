package dev.xkmc.modulargolems.compat.materials.goety.title;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class CooldownBypassModifier extends GolemModifier {

	public CooldownBypassModifier() {
		super(StatFilterType.ATTACK, 4);
	}

	@Override
	public void onAttackTarget(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		event.getEntity().invulnerableTime -= 5;
	}

}
