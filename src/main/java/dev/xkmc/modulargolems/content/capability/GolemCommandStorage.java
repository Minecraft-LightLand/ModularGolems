package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;

@SerialClass
public class GolemCommandStorage {

	public static Capability<GolemCommandStorage> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
	});

	public static GolemCommandStorage get(Level level) {
		if (level instanceof ServerLevel sl)
			return sl.getServer().overworld().getCapability(CAPABILITY).resolve().get();
		else return getClientCache(level);
	}

	private static GolemCommandStorage CACHE = null;

	private static GolemCommandStorage getClientCache(Level level) {
		if (CACHE == null || CACHE.level != level) {
			CACHE = new GolemCommandStorage(level);
		}
		return CACHE;
	}

	public final Level level;

	@SerialClass.SerialField
	private final HashMap<UUID, GolemCommandEntry[]> storage = new HashMap<>();

	public GolemCommandStorage(Level level) {
		this.level = level;
	}

	public GolemCommandEntry getOrCreateStorage(UUID id, int color, Component comp) {
		color = color & 15;
		GolemCommandEntry[] entries = storage.computeIfAbsent(id, k -> new GolemCommandEntry[16]);
		if (entries[color] == null) {
			entries[color] = GolemCommandEntry.getDefault(id, color, comp);
		}
		return entries[color];
	}

	@Nullable
	public GolemCommandEntry getStorage(UUID id, int color) {
		if (color < 0 || color > 15) return null;
		GolemCommandEntry[] entries = storage.computeIfAbsent(id, k -> new GolemCommandEntry[16]);
		return entries[color];
	}

	public void replaceStorage(GolemCommandEntry entry) {
		GolemCommandEntry[] entries = storage.computeIfAbsent(entry.getID(), k -> new GolemCommandEntry[16]);
		entries[entry.getColor()] = entry;
	}

	public void init() {

	}

}
