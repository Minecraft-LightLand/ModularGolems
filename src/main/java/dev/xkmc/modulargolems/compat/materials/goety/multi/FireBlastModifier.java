package dev.xkmc.modulargolems.compat.materials.goety.multi;

import dev.xkmc.modulargolems.compat.materials.goety.modifier.IApostleModifier;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

public class FireBlastModifier extends GolemModifier implements IApostleModifier {

	public FireBlastModifier() {
		super(StatFilterType.MASS, 3);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new FireBlastGoal(entity, lv));
	}

}
