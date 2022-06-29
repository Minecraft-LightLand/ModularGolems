package dev.xkmc.modulargolems.content.core;

import dev.xkmc.l2library.base.NamedEntry;
import dev.xkmc.modulargolems.content.entity.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.registrate.GolemTypeRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;

import java.util.Objects;
import java.util.function.Supplier;

public class GolemType<T extends AbstractGolemEntity<T>> extends NamedEntry<GolemType<?>> {

	private final Supplier<EntityType<T>> type;

	public GolemType(Supplier<EntityType<T>> type) {
		super(GolemTypeRegistry.TYPES);
		this.type = type;
	}

	public T create(ServerLevel level) {
		return Objects.requireNonNull(type.get().create(level));
	}

}
