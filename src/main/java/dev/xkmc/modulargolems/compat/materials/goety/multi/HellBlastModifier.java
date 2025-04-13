package dev.xkmc.modulargolems.compat.materials.goety.multi;

import dev.xkmc.modulargolems.compat.materials.goety.modifier.IApostleModifier;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

public class HellBlastModifier extends GolemModifier implements IApostleModifier {

	public HellBlastModifier() {
		super(StatFilterType.MASS, 3);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(3, new HellBlastGoal(entity, lv));
	}

}
