package dev.xkmc.modulargolems.compat.materials.iceandfire.modifiers;

import dev.xkmc.modulargolems.compat.materials.iceandfire.proxy.IAFProxy;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

public class IceDragonDefenseModifier extends GolemModifier {

	public IceDragonDefenseModifier() {
		super(StatFilterType.HEALTH, 5);
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, LivingDamageEvent event, int level) {
		if (event.getSource().getEntity() instanceof LivingEntity le && le.distanceTo(entity) < 6)
			IAFProxy.get().iceHit(le, entity, level);
	}
}
