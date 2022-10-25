package dev.xkmc.modulargolems.content.entity.goals;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.registrate.GolemModifierRegistry;
import net.minecraft.world.entity.ai.goal.FloatGoal;

public class GolemFloatGoal extends FloatGoal {

	private final AbstractGolemEntity<?, ?> golem;

	public GolemFloatGoal(AbstractGolemEntity<?, ?> golem) {
		super(golem);
		this.golem = golem;
	}

	@Override
	public boolean canUse() {
		return golem.getModifiers().getOrDefault(GolemModifierRegistry.FLOAT.get(), 0) > 0 && super.canUse();
	}
}
