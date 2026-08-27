package dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator;

import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
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

	@Override
	public boolean canExistOn(GolemPart<?, ?> part) {
		return part == GolemItems.GOLEM_ARM.get();
	}
}
