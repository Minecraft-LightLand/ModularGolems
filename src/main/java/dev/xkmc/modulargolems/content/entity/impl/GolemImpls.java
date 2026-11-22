package dev.xkmc.modulargolems.content.entity.impl;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.PowerableMob;

import javax.annotation.Nullable;

public interface GolemImpls extends
		PowerableMob, TransientStateContainer.View,
		GolemReforgeImpl, GolemConfigImpl, GolemTrackerImpl, GolemInvManager.View, GolemPermissionImpl, GolemTargetImpl,
		GolemBeamImpl {

	@Nullable
	default AbstractGolemEntity<?, ?> getGolemController() {
		var p = asGolem().getControllingPassenger();
		return p instanceof AbstractGolemEntity<?, ?> g ? g : null;
	}

}
