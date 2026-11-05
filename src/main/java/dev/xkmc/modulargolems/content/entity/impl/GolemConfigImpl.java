package dev.xkmc.modulargolems.content.entity.impl;

import dev.xkmc.modulargolems.content.capability.GolemConfigEntry;
import dev.xkmc.modulargolems.content.capability.GolemConfigStorage;
import dev.xkmc.modulargolems.content.capability.PathConfig;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.hostile.HostileGolemRegistry;
import dev.xkmc.modulargolems.content.entity.mode.GolemMode;
import dev.xkmc.modulargolems.content.entity.mode.GolemModes;
import dev.xkmc.modulargolems.content.item.card.PathRecordCard;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GolemConfigImpl extends GolemImplBase {

	default int getConfigColor() {
		return entityData().get(AbstractGolemEntity.CONFIG_COLOR);
	}

	@Nullable
	default GolemConfigEntry getConfigEntry(@Nullable Component dummy) {
		var self = asGolem();
		int configColor = getConfigColor();
		var opt = HostileGolemRegistry.tryGetFaction(self);
		if (opt.isPresent()) {
			return opt.get().getConfig(self, configColor);
		}
		UUID configOwner = entityData().get(AbstractGolemEntity.CONFIG_ID).orElse(null);
		if (configColor < 0 || configOwner == null) return null;
		var storage = GolemConfigStorage.get(self.level());
		if (dummy == null) {
			return storage.getStorage(configOwner, configColor);
		} else {
			return storage.getOrCreateStorage(configOwner, configColor, dummy);
		}
	}

	default void setConfigCard(@Nullable UUID owner, int color) {
		entityData().set(AbstractGolemEntity.CONFIG_ID, Optional.ofNullable(owner));
		entityData().set(AbstractGolemEntity.CONFIG_COLOR, color);
	}

	default void setPatrolStage(int stage) {
		entityData().set(AbstractGolemEntity.PATROL_STAGE, stage);
	}

	default int getPatrolStage() {
		return entityData().get(AbstractGolemEntity.PATROL_STAGE);
	}

	default void advancePatrolStage() {
		var list = PathConfig.getPath(asGolem());
		if (list == null) return;
		int stage = getPatrolStage();
		stage++;
		if (stage >= list.size()) {
			stage = 0;
		}
		setPatrolStage(stage);
	}

	default List<PathRecordCard.Pos> getPatrolList() {
		var list = PathConfig.getPath(asGolem());
		if (list == null) return List.of();
		int stage = getPatrolStage();
		if (stage > 0 && stage < list.size()) {
			var first = list.subList(stage, list.size());
			var second = list.subList(0, stage);
			var ans = new ArrayList<>(first);
			ans.addAll(second);
			return ans;
		}
		return list;
	}

	@Nullable
	default LivingEntity getFollowTarget() {
		var self = asGolem();
		if (self.getMode() == GolemModes.SQUAD) {
			return getCaptain();
		}
		var leader = self.getLeader();
		if (leader != null) return leader;
		return self.getOwner();
	}

	@Nullable
	default LivingEntity getCaptain() {
		if (asGolem().level() instanceof ServerLevel sl) {
			var config = getConfigEntry(null);
			if (config == null) return null;
			var uuid = config.squadConfig.getCaptainId();
			if (uuid == null) return null;
			var captain = sl.getEntity(uuid);
			if (captain == null) return null;
			if (!captain.isAlive() || captain.level() != sl) return null;
			if (captain instanceof LivingEntity le) {
				return le;
			} else return null;
		} else return null;
	}

	default Vec3 getTargetPos() {
		var self = asGolem();
		var dim = self.level().dimension().location();
		if (self.getMode() == GolemModes.ROUTE) {
			var list = PathConfig.getPath(self);
			if (list != null) {
				int target = getPatrolStage();
				if (!list.isEmpty()) {
					var pos = list.get(Math.min(target, list.size() - 1));
					if (pos.level().equals(dim))
						return pos.pos().getCenter();
				}
			}
			return self.position();
		}
		if (self.getMode().hasPos()) {
			BlockPos pos = self.getGuardPos();
			return new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
		}
		LivingEntity owner = getFollowTarget();
		if (owner == null) return self.getPosition(1);
		return owner.getPosition(1);
	}

	default GolemMode getMode() {
		return GolemModes.get(entityData().get(AbstractGolemEntity.DATA_MODE));
	}

	default BlockPos getGuardPos() {
		return entityData().get(AbstractGolemEntity.GUARD_POS);
	}

	default void setMode(int mode, BlockPos pos) {
		entityData().set(AbstractGolemEntity.DATA_MODE, mode);
		entityData().set(AbstractGolemEntity.GUARD_POS, pos);
	}


}
