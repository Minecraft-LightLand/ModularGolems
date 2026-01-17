package dev.xkmc.modulargolems.compat.materials.compositematerial.modifier;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;

public class ResonantHealModifier extends GolemModifier {

	public ResonantHealModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onHealPost(float heal, AbstractGolemEntity<?, ?> golem, int value) {
		var val = heal * 0.2f * value;//TODO config
		var level = golem.level();
		var aabb = golem.getBoundingBox().inflate(32);//TODO config
		var list = level.getEntitiesOfClass(AbstractGolemEntity.class, aabb);
		for (var e : list) {
			if (e == golem || !e.isAlliedTo(golem)) continue;
			e.heal(val);
		}
	}

}
