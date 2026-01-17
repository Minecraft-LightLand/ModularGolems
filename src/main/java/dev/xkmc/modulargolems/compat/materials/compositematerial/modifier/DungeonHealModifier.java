package dev.xkmc.modulargolems.compat.materials.compositematerial.modifier;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;

public class DungeonHealModifier extends GolemModifier {

	private static boolean recursive = false;

	public DungeonHealModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onHealPost(float heal, AbstractGolemEntity<?, ?> golem, int value) {
		if (recursive) return;
		recursive = true;
		var val = heal * 0.2f * value;//TODO config
		var player = golem.getOwner();
		if (player == null) return;
		player.heal(val);
		recursive = false;
	}

}
