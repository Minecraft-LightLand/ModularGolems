package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.l2serial.serialization.SerialClass;
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

	@SerialClass.SerialField
	public final LinkedHashMap<UUID, TrackedData> data = new LinkedHashMap<>();

	public void track(AbstractGolemEntity<?, ?> e) {
		if (!e.isAddedToWorld()) return;
		if (e.isDeadOrDying()) return;
		data.computeIfAbsent(e.getUUID(), k -> new TrackedData()).update(e);
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

		@SerialClass.SerialField
		public Status status = Status.ALIVE;
		@SerialClass.SerialField
		public ResourceLocation lastDim;
		@SerialClass.SerialField
		public BlockPos lastPos;
		@SerialClass.SerialField
		public long timestamp;
		@SerialClass.SerialField
		public String name, cause;
		@SerialClass.SerialField
		public float mhp, hp;
		@SerialClass.SerialField
		public GolemType<?, ?> golemType;
		@SerialClass.SerialField
		public List<ResourceLocation> materials = new ArrayList<>();

		public void update(AbstractGolemEntity<?, ?> e) {
			status = Status.ALIVE;
			lastDim = e.level().dimension().location();
			lastPos = e.blockPosition();
			timestamp = e.level().getGameTime();
			cause = null;
			mhp = e.getMaxHealth();
			hp = e.getHealth();
			if (name == null || e.tickCount % 20 == 10) {
				name = Component.Serializer.toJson(e.getName());
				golemType = GolemType.getGolemType(e.getType());
				materials.clear();
				for (var m : e.getMaterials()) {
					materials.add(m.id());
				}
			}
		}

		public void untrack(AbstractGolemEntity<?, ?> e, Status type, @Nullable Entity cause) {
			if (lastDim == null)
				update(e);
			if (!type.isDeath() || !status.isDeath())
				status = type;
			hp = e.getHealth();
			if (cause != null)
				this.cause = Component.Serializer.toJson(cause.getName());
		}

	}

}
