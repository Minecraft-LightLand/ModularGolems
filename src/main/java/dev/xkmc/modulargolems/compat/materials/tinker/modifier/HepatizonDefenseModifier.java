package dev.xkmc.modulargolems.compat.materials.tinker.modifier;

import dev.xkmc.l2library.base.effects.EffectUtil;
import dev.xkmc.modulargolems.compat.materials.tinker.TCCompatRegistry;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

public class HepatizonDefenseModifier extends GolemModifier {

	public HepatizonDefenseModifier() {
		super(StatFilterType.HEALTH, 5);
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, LivingDamageEvent event, int level) {
		TCCompatRegistry.EFF_HEPATIZON.get().addTo(entity, 100, level - 1, EffectUtil.AddReason.SELF, entity);
	}

}
