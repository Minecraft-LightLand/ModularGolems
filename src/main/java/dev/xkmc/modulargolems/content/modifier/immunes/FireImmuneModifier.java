package dev.xkmc.modulargolems.content.modifier.immunes;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.GolemModifier;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.function.BiConsumer;

public class FireImmuneModifier extends GolemModifier {

	public FireImmuneModifier() {
		super(StatFilterType.HEALTH, 1);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		entity.addFlag(GolemFlags.FIRE_IMMUNE);
	}

	@Override
	public void onAttacked(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		if (level > 0 && event.getSource().isFire()) {
			event.setCanceled(true);
		}
	}

}
