package dev.xkmc.modulargolems.content.entity.sync;

import dev.xkmc.l2library.util.nbt.NBTObj;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class SyncedData {

	public static final Serializer<Integer> INT;
	public static final Serializer<BlockPos> BLOCK_POS;
	public static final Serializer<Optional<UUID>> UUID;

	static {
		INT = new Serializer<>(EntityDataSerializers.INT, IntTag::valueOf, tag -> tag instanceof NumericTag n ? n.getAsInt() : 0);
		BLOCK_POS = new Serializer<>(EntityDataSerializers.BLOCK_POS, pos -> {
			var ans = new NBTObj();
			ans.fromBlockPos(pos);
			return ans.tag;
		}, tag -> tag instanceof CompoundTag ct ? new NBTObj(ct).toBlockPos() : BlockPos.ZERO);
		UUID = new Serializer<>(EntityDataSerializers.OPTIONAL_UUID, uuid -> uuid.map(NbtUtils::createUUID).orElse(null),
				tag -> Optional.ofNullable(tag).map(NbtUtils::loadUUID));
	}

	private final Definer cls;

	private final List<Data<?>> list = new ArrayList<>();

	public SyncedData(Definer cls) {
		this.cls = cls;
	}

	public void register(SynchedEntityData data) {
		for (Data<?> entry : list) {
			entry.register(data);
		}
	}

	public <T> EntityDataAccessor<T> define(Serializer<T> ser, T init, @Nullable String name) {
		Data<T> data = new Data<>(ser, init, name);
		list.add(data);
		return data.data;
	}

	public void write(CompoundTag tag, SynchedEntityData entityData) {
		for (Data<?> entry : list) {
			entry.write(tag, entityData);
		}
	}

	public void read(CompoundTag tag, SynchedEntityData entityData) {
		for (Data<?> entry : list) {
			entry.read(tag, entityData);
		}
	}

	private class Data<T> {

		private final Serializer<T> ser;
		private final EntityDataAccessor<T> data;
		private final T init;
		private final String name;

		private Data(Serializer<T> ser, T init, @Nullable String name) {
			this.ser = ser;
			this.data = cls.define(ser.ser());
			this.init = init;
			this.name = name;
		}

		private void register(SynchedEntityData data) {
			data.define(this.data, this.init);
		}

		public void write(CompoundTag tag, SynchedEntityData entityData) {
			if (name == null) return;
			Tag ans = ser.write(entityData.get(data));
			if (ans != null) tag.put(name, ans);
		}

		public void read(CompoundTag tag, SynchedEntityData entityData) {
			if (name == null) return;
			entityData.set(data, ser.read(tag.get(name)));
		}
	}

	public record Serializer<T>(EntityDataSerializer<T> ser,
								Function<T, @Nullable Tag> write,
								Function<@Nullable Tag, T> read) {

		@Nullable
		public Tag write(T t) {
			return write.apply(t);
		}

		public T read(@Nullable Tag tag) {
			return read.apply(tag);
		}

	}

	public interface Definer {

		<T> EntityDataAccessor<T> define(EntityDataSerializer<T> ser);

	}

}
