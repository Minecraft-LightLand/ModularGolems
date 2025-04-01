package dev.xkmc.modulargolems.compat.materials.iceandfire.modifiers;

import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.modulargolems.compat.materials.iceandfire.proxy.IAFProxy;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;

public class LightningDragonAttackModifier extends GolemModifier {

	public LightningDragonAttackModifier() {
		super(StatFilterType.ATTACK, 5);
	}

	@Override
	public void postHurtTarget(AbstractGolemEntity<?, ?> entity, DamageData.DefenceMax event, int level) {
		IAFProxy.get().lightningHit(event.getTarget(), entity, level);
	}

}
