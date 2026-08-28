package dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

/**
 * Multi-target plasma orb modifier (Obliterator).
 * Each target receives one PlasmaOrbEntity, mirrors shootPlasmaBall multi-spawn.
 * Reference: TheObliteratorEntity state 22 with multiple plasma orbs
 */
public class ObliteratorPlasmaOrbModifier extends GolemModifier {

	public ObliteratorPlasmaOrbModifier() {
		super(StatFilterType.ATTACK, 3);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new ObliteratorPlasmaOrbGoal(entity, lv));
	}

	@Override
	public boolean canExistOn(GolemPart<?, ?> part) {
		return part.getEntityType() == GolemTypes.TYPE_GOLEM.get() && super.canExistOn(part);
	}

}
