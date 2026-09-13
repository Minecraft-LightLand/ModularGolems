package dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

/**
 * Paladin head modifier: throws three phantom daggers that home onto a single target.
 * Reference: PosessedPaladinEntity::throwPhantomDaggers (ThrownPhantomDaggerEntity).
 */
public class PhantomDaggerModifier extends GolemModifier {

	public PhantomDaggerModifier() {
		super(StatFilterType.HEAD, 1);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new PhantomDaggerGoal(entity, lv));
	}

}
