package dev.xkmc.modulargolems.content.entity.impl;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.hostile.HostileGolemRegistry;
import dev.xkmc.modulargolems.content.entity.targeting.TargetManager;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.*;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface GolemTargetImpl extends GolemImplBase, NeutralMob {

	default boolean predicateTarget(LivingEntity e) {
		return TargetManager.predicateTarget(asGolem(), e) != null;
	}

	default void postSetTarget(@Nullable LivingEntity prev, @Nullable LivingEntity target) {
		var self = asGolem();
		if (target != null) {
			TargetManager.get(self).onSetTarget(self, target);
		}
		if (target != prev && target instanceof Mob mob) {
			if (mob.getTarget() == null && mob.canAttack(self)) {
				mob.setTarget(self);
			}
			for (var entry : self.getModifiers().entrySet()) {
				entry.getKey().onSetTarget(self, mob, entry.getValue());
			}
		}
	}

	default boolean canAttackImpl(LivingEntity target, boolean def) {
		var self = asGolem();
		var owner = self.getOwner();
		var leader = self.getLeader();
		if (target == owner || target == leader)
			return false;
		if (target instanceof OwnableEntity own) {
			var parent = own.getOwner();
			if (parent != null && (owner == parent || leader == parent)) {
				return false;
			}
		}
		if (!target.canBeSeenAsEnemy()) return false;
		if (!target.isAlive()) return false;
		if (target instanceof AbstractGolemEntity<?, ?> other) {
			if (self.isHostile() != other.isHostile()) return true;
			if (self.isHostile() && other.isHostile() && self.getOwnerUUID() == other.getOwnerUUID()) {
				return false;
			}
		}
		var faction = HostileGolemRegistry.tryGetFaction(self);
		if (faction.isPresent() && faction.get().hostileGolemAttacks(self, target)) {
			return true;
		}
		var config = self.getConfigEntry(null);
		if (config == null) {
			if (target.getType().is(MGTagGen.GOLEM_FRIENDLY)) {
				return false;
			}
		} else {
			if (config.targetFilter.friendlyToward(target)) {
				return false;
			}
		}
		return !self.isAlliedTo(target) && self.canAttackType(target.getType()) && def;
	}

	default boolean validSeeThroughTarget(Entity target) {
		Vec3 self = asGolem().getEyePosition();
		Vec3 tarp = new Vec3(target.getX(), target.getEyeY(), target.getZ());
		double dist = tarp.distanceTo(self);
		if (dist <= 128.0D) {
			if (target.level().canSeeSky(target.blockPosition()))
				return true;
			if (dist < 5)
				return true;
			if (self.y() < tarp.y())
				return true;
		}
		return false;
	}

	UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);

	default void startPersistentAngerTimer() {
		setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(asGolem().getRandom()));
	}

	default int getRemainingPersistentAngerTime() {
		return entityData().get(AbstractGolemEntity.DATA_REMAINING_ANGER_TIME);
	}

	default void setRemainingPersistentAngerTime(int pTime) {
		entityData().set(AbstractGolemEntity.DATA_REMAINING_ANGER_TIME, pTime);
	}

	default void setPersistentAngerTarget(@Nullable UUID target) {
		asGolem().transientManager.persistentAngerTarget = target;
	}

	@Nullable
	default UUID getPersistentAngerTarget() {
		return asGolem().transientManager.persistentAngerTarget;
	}

	default void tickTarget() {
		var self = asGolem();
		this.updatePersistentAnger((ServerLevel) self.level(), true);
		if (self.getControllingPassenger() == null) {
			var target = getTarget();
			if (target != null && target.isAlive()) {
				TargetManager.get(self).tickTarget(self, target);
			}
		}
	}

}
