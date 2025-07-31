package dev.xkmc.modulargolems.content.entity.hostile;

import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HostileGolemRegistry {

	private static final Map<ResourceLocation, HostileFaction> HOSTILE_MAP = new ConcurrentHashMap<>();

	private static final Map<UUID, HostileFaction> UUID_MAP = new ConcurrentHashMap<>();

	public static final HostileFaction DEFAULT = register(new DefaultHostileFaction(ModularGolems.loc("default")));

	public static HostileFaction register(HostileFaction entry) {
		HOSTILE_MAP.put(entry.id, entry);
		UUID_MAP.put(entry.uuid, entry);
		return entry;
	}

	public static HostileFaction getFaction(ResourceLocation id) {
		return HOSTILE_MAP.get(id);
	}

	public static HostileFaction getFaction(UUID id) {
		return UUID_MAP.get(id);
	}

	public static boolean isHostile(@Nullable UUID id) {
		if (id == null) return false;
		return UUID_MAP.containsKey(id);
	}

}
