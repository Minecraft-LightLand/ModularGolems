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
public class GolemConfigStorage {

	public static Capability<GolemConfigStorage> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
	});

	public static GolemConfigStorage get(Level level) {
		if (level instanceof ServerLevel sl)
			return sl.getServer().overworld().getCapability(CAPABILITY).resolve().get();
		else return getClientCache(level);
	}

	private static GolemConfigStorage CACHE = null;

	private static GolemConfigStorage getClientCache(Level level) {
		if (CACHE == null || CACHE.level != level) {
			CACHE = new GolemConfigStorage(level);
		}
		return CACHE;
	}

	public final Level level;

	@SerialClass.SerialField
	private final HashMap<UUID, GolemConfigEntry[]> storage = new HashMap<>();

	public GolemConfigStorage(Level level) {
		this.level = level;
	}

	public GolemConfigEntry getOrCreateStorage(UUID id, int color, Component comp) {
		color = color & 15;
		GolemConfigEntry[] entries = storage.computeIfAbsent(id, k -> new GolemConfigEntry[16]);
		if (entries[color] == null) {
			entries[color] = GolemConfigEntry.getDefault(id, color, comp);
		}
		return entries[color].init(id, color);
	}

	@Nullable
	public GolemConfigEntry getStorage(UUID id, int color) {
		if (color < 0 || color > 15) return null;
		GolemConfigEntry[] entries = storage.computeIfAbsent(id, k -> new GolemConfigEntry[16]);
		var ans = entries[color];
		if (ans == null) return null;
		ans.init(id, color);
		return ans;
	}

	public void replaceStorage(GolemConfigEntry entry) {
		GolemConfigEntry[] entries = storage.computeIfAbsent(entry.getID(), k -> new GolemConfigEntry[16]);
		entries[entry.getColor()] = entry.copyFrom(entries[entry.getColor()]);
	}

	public void init() {

	}

	public static void register() {

	}

}
