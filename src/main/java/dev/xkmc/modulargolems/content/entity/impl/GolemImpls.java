package dev.xkmc.modulargolems.content.entity.impl;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.PowerableMob;

import javax.annotation.Nullable;

public interface GolemImpls extends
		PowerableMob, TransientStateContainer.View,
		GolemReforgeImpl, GolemConfigImpl, GolemTrackerImpl, GolemInvImpl, GolemPermissionImpl, GolemTargetImpl,
		GolemBeamImpl {

	@Override
	default boolean isPowered() {
		return true;
	}

	@Nullable
	default AbstractGolemEntity<?, ?> getGolemController() {
		var p = asGolem().getControllingPassenger();
		return p instanceof AbstractGolemEntity<?, ?> g ? g : null;
	}

	default boolean isRidingGolemCombo() {
		if (getGolemController() != null) return true;
		return asGolem().getVehicle() instanceof AbstractGolemEntity<?, ?> dog && dog.getControllingPassenger() == asGolem();
	}

}
