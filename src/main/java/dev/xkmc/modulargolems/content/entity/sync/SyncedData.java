package dev.xkmc.modulargolems.content.entity.sync;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SyncedData {

	public static final Serializer<Integer> INT;
	public static final Serializer<BlockPos> BLOCK_POS;
	public static final Serializer<Optional<UUID>> UUID;

	static {
		INT = new Serializer<>(EntityDataSerializers.INT, Codec.INT);
		BLOCK_POS = new Serializer<>(EntityDataSerializers.BLOCK_POS, BlockPos.CODEC);
		UUID = new Serializer<>(EntityDataSerializers.OPTIONAL_UUID, UUIDUtil.CODEC.xmap(Optional::of, Optional::get));
	}

	private final Definer cls;

	private final List<Data<?>> list = new ArrayList<>();

	public SyncedData(Definer cls) {
		this.cls = cls;
	}

	public void register(SynchedEntityData.Builder data) {
		for (Data<?> entry : list) {
			entry.register(data);
		}
	}

	public <T> EntityDataAccessor<T> define(Serializer<T> ser, T init, @Nullable String name) {
		Data<T> data = new Data<>(ser, init, name);
		list.add(data);
		return data.data;
	}

	public void write(RegistryAccess pvd, CompoundTag tag, SynchedEntityData entityData) {
		for (Data<?> entry : list) {
			entry.write(pvd, tag, entityData);
		}
	}

	public void read(RegistryAccess pvd, CompoundTag tag, SynchedEntityData entityData) {
		for (Data<?> entry : list) {
			entry.read(pvd, tag, entityData);
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

		private void register(SynchedEntityData.Builder data) {
			data.define(this.data, this.init);
		}

		public void write(RegistryAccess pvd, CompoundTag tag, SynchedEntityData entityData) {
			if (name == null) return;
			Tag ans = ser.write(pvd, entityData.get(data));
			if (ans != null) tag.put(name, ans);
		}

		public void read(RegistryAccess pvd, CompoundTag tag, SynchedEntityData entityData) {
			if (name == null) return;
			var in = tag.get(name);
			entityData.set(data, Optional.ofNullable(in).map(e -> ser.read(pvd, e)).orElse(init));
		}
	}

	public record Serializer<T>(EntityDataSerializer<T> ser, Codec<T> codec) {

		@Nullable
		public Tag write(RegistryAccess pvd, T t) {
			return codec.encodeStart(pvd.createSerializationContext(NbtOps.INSTANCE), t).getOrThrow();
		}

		@Nullable
		public T read(RegistryAccess pvd, Tag tag) {
			return codec.decode(pvd.createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow().getFirst();
		}

	}

	public interface Definer {

		<T> EntityDataAccessor<T> define(EntityDataSerializer<T> ser);

	}

}
