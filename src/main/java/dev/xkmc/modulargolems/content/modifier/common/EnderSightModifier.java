package dev.xkmc.modulargolems.content.modifier.common;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.GolemModifier;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

public class EnderSightModifier extends GolemModifier {

	public EnderSightModifier() {
		super(StatFilterType.HEALTH, 1);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		entity.addFlag(GolemFlags.SEE_THROUGH);
	}
}
