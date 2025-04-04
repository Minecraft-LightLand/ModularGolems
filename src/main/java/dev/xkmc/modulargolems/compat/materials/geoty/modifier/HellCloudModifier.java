package dev.xkmc.modulargolems.compat.materials.geoty.modifier;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

public class HellCloudModifier extends GolemModifier implements IApostleModifier {

	public HellCloudModifier() {
		super(StatFilterType.MASS, 5);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new HellCloudGoal(entity, lv));
	}

}
