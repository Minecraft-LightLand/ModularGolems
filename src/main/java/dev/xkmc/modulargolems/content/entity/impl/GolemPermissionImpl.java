package dev.xkmc.modulargolems.content.entity.impl;

import dev.xkmc.modulargolems.content.entity.hostile.HostileGolemRegistry;
import dev.xkmc.modulargolems.content.item.card.ConfigCard;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.UUID;

public interface GolemPermissionImpl extends GolemImplBase, OwnableEntity {

	@Nullable
	default Player getOwner() {
		try {
			UUID uuid = this.getOwnerUUID();
			return uuid == null ? null : this.level().getPlayerByUUID(uuid);
		} catch (IllegalArgumentException ignored) {
			return null;
		}
	}

	@Nullable
	default LivingEntity getLeader() {
		try {
			UUID uuid = asGolem().getLeaderUUID();
			if (uuid == null) return null;
			if (!(level() instanceof ServerLevel sl)) return null;
			var e = sl.getEntity(uuid);
			if (!(e instanceof LivingEntity le)) return null;
			return le;
		} catch (IllegalArgumentException ignored) {
			return null;
		}
	}

	default boolean canModify(Player player) {
		return canModify(player, true);
	}

	default boolean canWandModify(Player player) {
		boolean bypass = MGConfig.COMMON.wandBypassConfig.get() || ConfigCard.filterMatch(player, asGolem());
		return canModify(player, !bypass);
	}

	default boolean canModify(Player player, boolean checkLock) {
		var self = asGolem();
		var entry = self.getConfigEntry(null);
		if (checkLock && entry != null && entry.locked)
			return false;
		LivingEntity owner = self.getOwner();
		if (player == owner) {
			return true;
		}
		if (player.getAbilities().instabuild || self.getOwnerUUID() == null && !self.predicateTarget(player))
			return true;
		if (MGConfig.COMMON.ownerPickupOnly.get()) {
			return false;
		}
		return self.isAlliedTo(player);
	}

	default boolean isAlliedToImpl(Entity other, boolean def) {
		if (other == this) return true;
		var self = asGolem();
		LivingEntity owner = self.getOwner();
		if (other == owner) {
			return true;
		}
		if (owner != null) {
			return owner.isAlliedTo(other) || other.isAlliedTo(owner);
		}
		var opt = HostileGolemRegistry.tryGetFaction(self);
		if (opt.isPresent() && opt.get().isAlliedTo(self, other)) {
			return true;
		}
		return def;
	}

}
