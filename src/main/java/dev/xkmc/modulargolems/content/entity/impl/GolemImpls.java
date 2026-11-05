package dev.xkmc.modulargolems.content.entity.impl;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.hostile.HostileGolemRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PowerableMob;
import net.minecraft.world.entity.ai.attributes.Attributes;

import javax.annotation.Nullable;

public interface GolemImpls extends
		PowerableMob, TransientStateContainer.View,
		GolemReforgeImpl, GolemConfigImpl, GolemTrackerImpl, GolemInvManager.View, GolemPermissionImpl, GolemTargetImpl,
		GolemBeamImpl {

	default void checkRide(LivingEntity target) {
	}

	default boolean isInSittingPose() {
		return false;
	}

	default boolean hasRangeAttack() {
		return false;
	}

	default boolean canSweep() {
		return false;
	}

	@Override
	default boolean isPowered() {
		return true;
	}

	default boolean isHostile() {
		return HostileGolemRegistry.isHostile(getOwnerUUID());
	}

	default int getPreviewScale() {
		return 18;
	}

	default float getAttackDamage() {
		return (float) asGolem().getAttributeValue(Attributes.ATTACK_DAMAGE);
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
