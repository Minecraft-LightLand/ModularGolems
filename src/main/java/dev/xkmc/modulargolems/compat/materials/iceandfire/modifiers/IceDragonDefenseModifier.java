package dev.xkmc.modulargolems.compat.materials.iceandfire.modifiers;

import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.modulargolems.compat.materials.iceandfire.proxy.IAFProxy;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.LivingEntity;

public class IceDragonDefenseModifier extends GolemModifier {

	public IceDragonDefenseModifier() {
		super(StatFilterType.ATTACK, 5);
	}

	@Override
	public void postDamaged(AbstractGolemEntity<?, ?> entity, DamageData.DefenceMax event, int level) {
		if (event.getAttacker() instanceof LivingEntity le && le.distanceTo(entity) < 6)
			IAFProxy.get().iceHit(le, entity, level);
	}

}
