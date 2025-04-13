package dev.xkmc.modulargolems.compat.materials.goety.modifier;

import com.Polarice3.Goety.common.entities.projectiles.FireTornado;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.function.BiConsumer;

public class FireTornadoModifier extends GolemModifier implements IApostleModifier {

	public FireTornadoModifier() {
		super(StatFilterType.MASS, 5);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new FireTornadoGoal(entity, lv));
	}

	@Override
	public void onHurtTarget(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {
		if (event.getSource().getDirectEntity() instanceof FireTornado && event.getSource().getEntity() == entity) {
			event.setAmount(event.getAmount() * (1 + 0.5f * (level - 1)));
		}
	}

}
