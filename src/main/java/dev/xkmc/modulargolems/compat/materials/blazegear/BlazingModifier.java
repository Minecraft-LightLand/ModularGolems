package dev.xkmc.modulargolems.compat.materials.blazegear;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.BiConsumer;

public class BlazingModifier extends GolemModifier {

	public BlazingModifier() {
		super(StatFilterType.MASS, 3);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new BlazeAttackGoal(entity, lv));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onClientTick(AbstractGolemEntity<?, ?> entity, int value) {
		entity.level.addParticle(ParticleTypes.LARGE_SMOKE, entity.getRandomX(0.5D), entity.getRandomY(), entity.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
	}

}
