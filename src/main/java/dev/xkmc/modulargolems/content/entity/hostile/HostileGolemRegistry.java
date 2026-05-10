package dev.xkmc.modulargolems.content.entity.hostile;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HostileGolemRegistry {

	private static final Map<Identifier, HostileFaction> HOSTILE_MAP = new ConcurrentHashMap<>();

	private static final Map<UUID, HostileFaction> UUID_MAP = new ConcurrentHashMap<>();

	public static final HostileFaction DEFAULT = register(new DefaultHostileFaction(ModularGolems.loc("default")));

	public static HostileFaction register(HostileFaction entry) {
		HOSTILE_MAP.put(entry.id, entry);
		UUID_MAP.put(entry.uuid, entry);
		return entry;
	}

	public static HostileFaction getFaction(Identifier id) {
		return HOSTILE_MAP.get(id);
	}

	public static HostileFaction getFaction(UUID id) {
		return UUID_MAP.get(id);
	}

	public static Optional<HostileFaction> tryGetFaction(AbstractGolemEntity<?, ?> golem) {
		var uuid = golem.getOwnerUUID();
		if (uuid == null) return Optional.empty();
		return Optional.ofNullable(UUID_MAP.get(uuid));
	}

	public static boolean isHostile(@Nullable UUID id) {
		if (id == null) return false;
		return UUID_MAP.containsKey(id);
	}

}
