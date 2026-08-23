package dev.xkmc.modulargolems.compat.materials.eeeab.guardian;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

/**
 * Nameless Guardian - Attack 4: Smash / Lobed Blade Attack
 * Original: {@code GuardianLobedAttackGoal} - SMASHATTACK_ANIMATION (40 ticks)
 * Uses intermediate entity {@code EntityGuardianBlade} x8 when powered.
 * Decompiled: com/eeeab/eeeabsmobs/sever/entity/ai/goal/animate/GuardianLobedAttackGoal.java:59-70
 */
public class GuardianBladeModifier extends GolemModifier {

	public GuardianBladeModifier() {
		super(StatFilterType.ATTACK, 3);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new GuardianBladeAttackGoal(entity, lv));
	}

}
