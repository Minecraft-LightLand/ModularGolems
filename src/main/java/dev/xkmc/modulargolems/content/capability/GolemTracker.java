package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@SerialClass
public class GolemTracker {

	public enum Status {
		ALIVE, RETRIEVED, OTHER_RETRIEVED, DEATH, DEATH_RECYCLE;


		public boolean isDeath() {
			return this == DEATH || this == DEATH_RECYCLE;
		}

	}

	public enum RetrieveTarget {
		INVENTORY, ENDER, DIMENSIONAL
	}

	@SerialField
	public final LinkedHashMap<UUID, TrackedData> data = new LinkedHashMap<>();

	public void track(AbstractGolemEntity<?, ?> e) {
		if (!e.isAddedToLevel()) return;
		if (e.isDeadOrDying()) return;
		data.computeIfAbsent(e.getUUID(), k -> new TrackedData()).update(e);
	}

	public void trackPos(UUID id, double x, double y, double z) {
		var rec = data.get(id);
		if (rec != null) rec.updatePos(x, y, z);
	}

	public boolean isUntracked(AbstractGolemEntity<?, ?> e) {
		var rec = data.get(e.getUUID());
		if (rec == null) return true;
		return rec.status != Status.ALIVE;
	}

	public void untrack(AbstractGolemEntity<?, ?> e, Status type, @Nullable Entity cause) {
		data.computeIfAbsent(e.getUUID(), k -> new TrackedData()).untrack(e, type, cause);
	}

	@SerialClass
	public static class TrackedData {

		@SerialField
		public Status status = Status.ALIVE;
		@SerialField
		public ResourceLocation lastDim;
		@SerialField
		public BlockPos lastPos;
		@SerialField
		public long timestamp;
		@SerialField
		public Component name, cause;
		@SerialField
		public float mhp, hp;
		@SerialField
		public GolemType<?, ?> golemType;
		@SerialField
		public List<ResourceLocation> materials = new ArrayList<>();
		@SerialField
		public RetrieveTarget target = null;

		public void update(AbstractGolemEntity<?, ?> e) {
			status = Status.ALIVE;
			target = null;
			lastDim = e.level().dimension().location();
			lastPos = e.blockPosition();
			timestamp = e.level().getGameTime();
			cause = null;
			mhp = e.getMaxHealth();
			hp = e.getHealth();
			if (name == null || e.tickCount % 20 == 10) {
				name = e.getName();
				golemType = GolemType.getGolemType(e.getType());
				materials.clear();
				for (var m : e.getMaterials()) {
					materials.add(m.id());
				}
			}
		}

		public void updatePos(double x, double y, double z) {
			lastPos = BlockPos.containing(x, y, z);
		}

		public void untrack(AbstractGolemEntity<?, ?> e, Status type, @Nullable Entity cause) {
			if (lastDim == null)
				update(e);
			status = type;
			hp = e.getHealth();
			if (cause != null)
				this.cause = cause.getName();
		}

	}

}
