package dev.xkmc.modulargolems.compat.materials.iceandfire.modifiers;

import dev.xkmc.modulargolems.compat.materials.iceandfire.proxy.IAFProxy;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class LightningDragonAttackModifier extends GolemModifier {

	public LightningDragonAttackModifier() {
		super(StatFilterType.ATTACK, 5);
	}

	@Override
	public void onHurtTarget(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {
		IAFProxy.get().lightningHit(event.getEntity(), entity, level);
	}
}
