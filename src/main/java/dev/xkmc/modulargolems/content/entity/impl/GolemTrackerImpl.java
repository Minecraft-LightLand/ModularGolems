package dev.xkmc.modulargolems.content.entity.impl;

import dev.xkmc.modulargolems.content.capability.GolemConfigStorage;
import dev.xkmc.modulargolems.content.capability.GolemTracker;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.item.wand.GolemTransportHandler;
import dev.xkmc.modulargolems.events.event.GolemToOwnerEvent;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nullable;

public interface GolemTrackerImpl extends GolemImplBase {

	@Nullable
	default GolemTracker getTracker() {
		var self = asGolem();
		var id = self.getOwnerUUID();
		if (id == null || id.equals(Util.NIL_UUID) || self.isHostile()) return null;
		if (self.getOwner() instanceof FakePlayer) return null;
		return GolemConfigStorage.get(self.level()).getTracker(id);
	}

	default void tickTracker() {
		var tracker = getTracker();
		if (tracker != null)
			tracker.track(asGolem());
	}

	default void trackPos(double x, double y, double z) {
		var self = asGolem();
		if (self.level().isClientSide() || !self.isAddedToWorld()) return;
		var tracker = getTracker();
		if (tracker != null)
			tracker.trackPos(self.getUUID(), x, y, z);
	}

	default void setRetrivedTo(GolemTracker.RetrieveTarget target) {
		var tracker = getTracker();
		if (tracker == null) return;
		var data = tracker.data.get(asGolem().getUUID());
		if (data == null) return;
		data.target = target;
	}

	default void untrack(GolemTracker.Status type, @Nullable Entity cause) {
		var tracker = getTracker();
		if (tracker != null) tracker.untrack(asGolem(), type, cause);
	}

	default void markDead() {
		var self = asGolem();
		self.unRide();
		if (self.hasFlag(GolemFlags.RECYCLE)) {
			untrack(GolemTracker.Status.DEATH_RECYCLE, null);
			returnToInventory();
		} else {
			untrack(GolemTracker.Status.DEATH, null);
		}
		self.playDeathSound();
		self.level().broadcastEntityEvent(self, EntityEvent.POOF);
		self.discard();
	}

	default void returnToInventory() {
		var self = asGolem();
		var leader = self.getLeader();
		self.unRide();
		ItemStack stack = self.getAsStack();
		if (leader != null && leader.isAlive()) {
			if (MinecraftForge.EVENT_BUS.post(new GolemToOwnerEvent(leader, stack))) {
				return;
			}
		}
		Player player = self.getOwner();
		if (player != null && player.isAlive()) {
			if (MinecraftForge.EVENT_BUS.post(new GolemToOwnerEvent(player, stack))) {
				return;
			}
			if (player instanceof ServerPlayer sp)
				GolemTransportHandler.addGolemToPlayer(sp, stack, self);
		} else {
			self.spawnAtLocation(stack);
		}
	}

	default boolean untrackRemoved(@Nullable DamageSource source) {
		var self = asGolem();
		if (self.level().isClientSide()) return false;
		var tracker = getTracker();
		if (tracker == null || tracker.isUntracked(self)) return false;
		Player owner = self.getOwner();
		Entity cause = null;
		boolean sendMessages = MGConfig.COMMON.sendForceRemovalMessage.get();
		if (source != null) {
			ModularGolems.LOGGER.info("Golem {} died, message: '{}'", this, source.getLocalizedDeathMessage(self).getString());
			if (owner != null && sendMessages) {
				owner.sendSystemMessage(source.getLocalizedDeathMessage(self));
			}
			cause = source.getEntity();
		} else {
			ModularGolems.LOGGER.info("Golem {} is forcefully removed ", this);
			if (owner != null && sendMessages) {
				owner.sendSystemMessage(Component.literal("Golem " + this + " is forcefully removed"));
			}
			if (self.getLastHurtByMobTimestamp() == self.tickCount) {
				cause = self.getLastHurtByMob();
			}
		}
		if (self.getGuardedDataImpl() <= 0 && self.hasFlag(GolemFlags.RECYCLE)) {
			tracker.untrack(self, GolemTracker.Status.DEATH_RECYCLE, cause);
			returnToInventory();
			self.level().broadcastEntityEvent(self, EntityEvent.POOF);
			return true;
		} else {
			tracker.untrack(self, GolemTracker.Status.DEATH, cause);
			return false;
		}
	}

}
