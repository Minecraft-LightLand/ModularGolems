package dev.xkmc.modulargolems.compat.materials.l2complements.modifiers;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.GolemModifier;

public class ConduitModifier extends GolemModifier {

	public ConduitModifier() {
		super(StatFilterType.MASS, 4);
	}

	@Override
	public double onHealTick(double heal, AbstractGolemEntity<?, ?> entity, int level) {
		return heal;
	}

	@Override
	public void onAiStep(AbstractGolemEntity<?, ?> golem, int level) {

	}
}
