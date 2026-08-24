package dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

/**
 * Single-target laser modifier (Obliterator).
 * Proxy spawns one AnnihilationBeamEntity per attack.
 * Reference: TheObliteratorEntity states 42/43 single shot laser (single proxy) vs 46 quad (multi)
 */
public class ObliteratorLaserModifier extends GolemModifier {

	public ObliteratorLaserModifier() {
		super(StatFilterType.HEAD, 2);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new ObliteratorLaserGoal(entity, lv));
	}
}
