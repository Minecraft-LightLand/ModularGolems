package dev.xkmc.modulargolems.content.core;

import dev.xkmc.l2library.base.NamedEntry;
import dev.xkmc.l2library.repack.registrate.util.entry.EntityEntry;
import dev.xkmc.l2library.util.code.Wrappers;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.GolemHolder;
import dev.xkmc.modulargolems.init.registrate.GolemTypeRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Objects;

public class GolemType<T extends AbstractGolemEntity<T>> extends NamedEntry<GolemType<?>> {

	private static final HashMap<ResourceLocation, GolemType<?>> ENTITY_TYPE_TO_GOLEM_TYPE = new HashMap<>();
	public static final HashMap<ResourceLocation, GolemHolder<?>> GOLEM_TYPE_TO_ITEM = new HashMap<>();

	public static <T extends AbstractGolemEntity<T>> GolemType<T> getGolemType(EntityType<T> type) {
		return Wrappers.cast(ENTITY_TYPE_TO_GOLEM_TYPE.get(ForgeRegistries.ENTITY_TYPES.getKey(type)));
	}

	public static <T extends AbstractGolemEntity<T>> GolemHolder<T> getGolemHolder(GolemType<T> type) {
		return Wrappers.cast(GOLEM_TYPE_TO_ITEM.get(type.getRegistryName()));
	}

	public static <T extends AbstractGolemEntity<T>> GolemHolder<T> getGolemHolder(EntityType<T> type) {
		return getGolemHolder(getGolemType(type));
	}

	private final EntityEntry<T> type;

	public GolemType(EntityEntry<T> type) {
		super(GolemTypeRegistry.TYPES);
		this.type = type;
		ENTITY_TYPE_TO_GOLEM_TYPE.put(type.getId(), this);
	}

	public T create(ServerLevel level) {
		return Objects.requireNonNull(type.get().create(level));
	}

	public T create(ServerLevel level, CompoundTag tag) {
		return Wrappers.cast(EntityType.create(tag, level).get());
	}

}
