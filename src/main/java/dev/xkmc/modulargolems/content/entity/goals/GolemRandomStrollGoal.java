package dev.xkmc.modulargolems.content.entity.goals;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

public class GolemRandomStrollGoal extends RandomStrollGoal {

	private final AbstractGolemEntity<?, ?> golem;

	public GolemRandomStrollGoal(AbstractGolemEntity<?, ?> golem) {
		super(golem, 0.7, 60);
		this.golem = golem;
	}

	@Override
	public boolean canUse() {
		return golem.getMode().couldRandomStroll() && super.canUse();
	}

	@Override
	public boolean canContinueToUse() {
		return golem.getMode().couldRandomStroll() && super.canContinueToUse();
	}

	protected Vec3 getPosition() {
		if (this.mob.isInWaterOrBubble()) {
			if (golem.hasFlag(GolemFlags.SWIM)) {
				return BehaviorUtils.getRandomSwimmablePos(this.mob, 10, 7);
			}
			Vec3 vec3 = LandRandomPos.getPos(this.mob, 15, 7);
			return vec3 == null ? super.getPosition() : vec3;
		} else {
			return LandRandomPos.getPos(this.mob, 10, 7);
		}
	}

}
