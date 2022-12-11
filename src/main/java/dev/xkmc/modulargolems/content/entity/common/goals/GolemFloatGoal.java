package dev.xkmc.modulargolems.content.entity.common.goals;

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
		boolean canSwim = golem.getModifiers().getOrDefault(GolemModifierRegistry.SWIM.get(), 0) > 0;
		boolean canFloat = golem.getModifiers().getOrDefault(GolemModifierRegistry.FLOAT.get(), 0) > 0;

		if (golem.isInWater() && canSwim) {
			var target = golem.getTarget();
			if (target != null && target.isInWater())
				return false;
			if (golem.getDeltaMovement().y() > 0.01)
				return false;
		}
		return (canSwim || canFloat) && super.canUse();
	}
}
