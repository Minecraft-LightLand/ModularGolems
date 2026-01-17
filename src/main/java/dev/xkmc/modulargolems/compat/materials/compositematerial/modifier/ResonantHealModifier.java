package dev.xkmc.modulargolems.compat.materials.compositematerial.modifier;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;

public class ResonantHealModifier extends GolemModifier {

	private static boolean recursive = false;

	public ResonantHealModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onHealPost(float heal, AbstractGolemEntity<?, ?> golem, int value) {
		if (recursive) return;
		recursive = true;
		var val = heal * 0.05f * value;//TODO config
		var level = golem.level();
		var aabb = golem.getBoundingBox().inflate(32);//TODO config
		var list = level.getEntitiesOfClass(AbstractGolemEntity.class, aabb);
		for (var e : list) {
			if (e == golem || !e.isAlliedTo(golem)) continue;
			if (!e.getModifiers().containsKey(this)) continue;
			int lvl = (Integer) e.getModifiers().get(this);
			e.heal(val * lvl);
		}
		recursive = false;
	}

}
