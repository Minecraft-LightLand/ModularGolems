package dev.xkmc.modulargolems.compat.materials.goety.title;

import dev.xkmc.modulargolems.compat.materials.goety.modifier.IApostleGoal;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;

public class FastSkillModifier extends GolemModifier {

	public FastSkillModifier() {
		super(StatFilterType.MASS, 1);
	}

	@Override
	public void onAiStep(AbstractGolemEntity<?, ?> golem, int level) {
		for (var e : golem.goalSelector.getAvailableGoals()) {
			var goal = e.getGoal();
			if (goal instanceof BaseRangedAttackGoal ranged && goal instanceof IApostleGoal) {
				ranged.attackTime -= level;
			}
		}
	}

}
