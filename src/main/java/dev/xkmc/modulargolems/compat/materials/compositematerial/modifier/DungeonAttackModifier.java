package dev.xkmc.modulargolems.compat.materials.compositematerial.modifier;

import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;

public class DungeonAttackModifier extends GolemModifier {

	public DungeonAttackModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void finalizeHurtTarget(AttackCache cache, AbstractGolemEntity<?, ?> golem, int value) {
		golem.heal(cache.getDamageDealt() * 0.5f * value);//TODO config
	}

}
