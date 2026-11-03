package dev.xkmc.modulargolems.content.entity.impl;

import net.minecraft.world.entity.PowerableMob;

public interface GolemImpls extends
		PowerableMob, TransientStateContainer.View,
		GolemReforgeImpl, GolemConfigImpl, GolemTrackerImpl, GolemInvImpl, GolemPermissionImpl, GolemTargetImpl,
		GolemBeamImpl {

	@Override
	default boolean isPowered() {
		return true;
	}

}
