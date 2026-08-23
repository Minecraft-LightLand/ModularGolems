package dev.xkmc.modulargolems.compat.materials.eeeab.guardian;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

/**
 * Nameless Guardian - Attack 7: Laser Beam
 * Original: {@code GuardianShootLaserGoal} - LASER_ANIMATION (120 ticks)
 * Uses intermediate entity {@code EntityGuardianLaser}
 * Decompiled: com/eeeab/eeeabsmobs/sever/entity/ai/goal/animate/GuardianShootLaserGoal.java:42-48
 */
public class GuardianLaserModifier extends GolemModifier {

	public GuardianLaserModifier() {
		super(StatFilterType.ATTACK, 3);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new GuardianLaserAttackGoal(entity, lv));
	}

}
