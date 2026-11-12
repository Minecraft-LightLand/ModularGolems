package dev.xkmc.modulargolems.content.modifier.special;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

/**
 * The base class already asks the golem part config, so which golem types may carry this is decided
 * by the material data rather than being hard coded here.
 */
public class SonicModifier extends GolemModifier {

	public SonicModifier() {
		super(StatFilterType.HEAD, 1);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new SonicAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}


}
