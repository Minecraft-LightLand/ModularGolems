package dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

/**
 * Multi-target small bomb modifier (Obliterator).
 * Each target receives one SmallAnnihilationBombEntity, attack hits multiple targets.
 * Reference: TheObliteratorEntity state 7 double bomb, shootAngledBombs with bombCount>1
 */
public class ObliteratorSmallBombModifier extends GolemModifier {

	public ObliteratorSmallBombModifier() {
		super(StatFilterType.ATTACK, 3);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new ObliteratorSmallBombGoal(entity, lv));
	}
}
