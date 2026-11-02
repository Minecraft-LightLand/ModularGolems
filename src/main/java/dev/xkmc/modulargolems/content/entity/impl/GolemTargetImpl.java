package dev.xkmc.modulargolems.content.entity.impl;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.hostile.HostileGolemRegistry;
import dev.xkmc.modulargolems.content.entity.targeting.TargetManager;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface GolemTargetImpl extends GolemImplBase {

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

}
