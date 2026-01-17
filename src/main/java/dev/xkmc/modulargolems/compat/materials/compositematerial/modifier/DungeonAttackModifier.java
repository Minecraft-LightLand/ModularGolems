package dev.xkmc.modulargolems.compat.materials.compositematerial.modifier;

import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.init.data.L2DamageTypes;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;

public class DungeonAttackModifier extends GolemModifier {

	public DungeonAttackModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void finalizeHurtTarget(AttackCache cache, AbstractGolemEntity<?, ?> golem, int value) {
		var event = cache.getLivingAttackEvent();
		if (event == null) return;
		var source = event.getSource();
		if (!source.is(L2DamageTypes.DIRECT)) return;
		golem.heal(cache.getDamageDealt() * 0.25f * value);//TODO config
	}

}
