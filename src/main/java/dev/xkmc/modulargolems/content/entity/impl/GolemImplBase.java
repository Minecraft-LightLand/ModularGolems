package dev.xkmc.modulargolems.content.entity.impl;

import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.network.syncher.SynchedEntityData;

public interface GolemImplBase {

	default AbstractGolemEntity<?, ?> asGolem() {
		return Wrappers.cast(this);
	}

	SynchedEntityData entityData();

}
