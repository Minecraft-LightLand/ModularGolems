package dev.xkmc.modulargolems.content.entity.goals;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.goal.FloatGoal;

public class GolemFloatGoal extends FloatGoal {

	private final AbstractGolemEntity<?, ?> golem;

	public GolemFloatGoal(AbstractGolemEntity<?, ?> golem) {
		super(golem);
		this.golem = golem;
	}

	@Override
	public boolean canUse() {
		AbstractGolemEntity<?, ?> vehGolem = golem.getVehicle() instanceof AbstractGolemEntity<?, ?> e ? e : null;
		boolean canSwim = golem.getModifiers().getOrDefault(GolemModifiers.SWIM.get(), 0) > 0 ||
				vehGolem != null && vehGolem.getModifiers().getOrDefault(GolemModifiers.SWIM.get(), 0) > 0;
		boolean canFloat = golem.getModifiers().getOrDefault(GolemModifiers.FLOAT.get(), 0) > 0 ||
				vehGolem != null && vehGolem.getModifiers().getOrDefault(GolemModifiers.FLOAT.get(), 0) > 0;
		boolean fireImmune = golem.hasFlag(GolemFlags.FIRE_IMMUNE) || vehGolem != null && vehGolem.hasFlag(GolemFlags.FIRE_IMMUNE);

		AbstractGolemEntity<?, ?> e = vehGolem != null ? vehGolem : golem;

		if (e.isInWater() && canSwim) {
			var target = golem.getTarget();
			if (target != null && target.isInWater())
				return false;
			if (target == null && golem.getOwner() != null && golem.getOwner().getY() < golem.getY() + 2) {
				return false;
			}
			if (golem.getDeltaMovement().y() > 0.01)
				return false;
		}
		if (e.isInLava()) return fireImmune;
		return (canSwim || canFloat) && (e.isInWater() && e.getFluidHeight(FluidTags.WATER) > e.getFluidJumpThreshold() ||
				e.isInFluidType((fluidType, height) -> e.canSwimInFluidType(fluidType) && height > e.getFluidJumpThreshold()));
	}

	@Override
	public void tick() {
		AbstractGolemEntity<?, ?> vehGolem = golem.getVehicle() instanceof AbstractGolemEntity<?, ?> e ? e : null;
		AbstractGolemEntity<?, ?> e = vehGolem != null ? vehGolem : golem;
		if (e.getRandom().nextFloat() < 0.8F) {
			e.getJumpControl().jump();
		}
	}
}
