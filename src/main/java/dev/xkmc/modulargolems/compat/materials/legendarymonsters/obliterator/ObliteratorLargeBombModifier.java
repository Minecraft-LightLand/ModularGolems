package dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

/**
 * Single-target large bomb modifier (Obliterator).
 * Proxy spawns AnnihilationBombEntity once per attack.
 * Reference: TheObliteratorEntity state 6 / 55 shootAnnihilationBomb
 */
public class ObliteratorLargeBombModifier extends GolemModifier {

	public ObliteratorLargeBombModifier() {
		super(StatFilterType.ATTACK, 3);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new ObliteratorLargeBombGoal(entity, lv));
	}
}
