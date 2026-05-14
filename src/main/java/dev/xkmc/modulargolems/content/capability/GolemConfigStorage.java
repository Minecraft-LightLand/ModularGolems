package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.l2core.capability.level.BaseSavedData;
import dev.xkmc.l2serial.serialization.codec.CodecAdaptor;
import dev.xkmc.l2serial.serialization.codec.TagCodec;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

@SerialClass
public class GolemConfigStorage extends BaseSavedData<GolemConfigStorage> {

	private static final Identifier ID = ModularGolems.loc("configcards");
	private static final SavedDataType<GolemConfigStorage> FACTORY = new SavedDataType<>(ID, GolemConfigStorage::new, new CodecAdaptor<>(GolemConfigStorage.class));

	@Nullable
	private static GolemConfigStorage CLIENT;

	public static GolemConfigStorage get(Level level) {
		if (level instanceof ServerLevel sl) {
			sl = sl.getServer().overworld();
			GolemConfigStorage ans = sl.getDataStorage().computeIfAbsent(FACTORY);
			ans.level = sl;
			return ans;
		} else {
			if (CLIENT == null || CLIENT.level != level) {
				CLIENT = new GolemConfigStorage();
				CLIENT.level = level;
			}
			return CLIENT;
		}
	}

	private Level level;

	@SerialField
	private final HashMap<UUID, GolemConfigEntry @Nullable []> storage = new HashMap<>();
	@SerialField
	private final HashMap<UUID, GolemTracker> tracker = new HashMap<>();

	public GolemConfigStorage() {
		super(GolemConfigStorage.class);
	}

	public GolemConfigStorage(CompoundTag tag, HolderLookup.Provider pvd) {
		this();
		new TagCodec(pvd).fromTag(tag, GolemConfigStorage.class, this);
	}

	public GolemConfigEntry getOrCreateStorage(UUID id, int color, Component comp) {
		color = color & 15;
		var entries = storage.computeIfAbsent(id, k -> new GolemConfigEntry[16]);
		if (entries[color] == null) {
			entries[color] = GolemConfigEntry.getDefault(id, color, comp);
		}
		return entries[color].init(id, color);
	}

	public GolemTracker getTracker(UUID id) {
		return tracker.computeIfAbsent(id, k -> new GolemTracker());
	}

	@Nullable
	public GolemConfigEntry getStorage(UUID id, int color) {
		if (color < 0 || color > 15) return null;
		var entries = storage.computeIfAbsent(id, k -> new GolemConfigEntry[16]);
		var ans = entries[color];
		if (ans == null) return null;
		ans.init(id, color);
		return ans;
	}

	public void replaceStorage(GolemConfigEntry entry) {
		var entries = storage.computeIfAbsent(entry.getID(), k -> new GolemConfigEntry[16]);
		entries[entry.getColor()] = entry.copyFrom(entries[entry.getColor()]);
	}

	public void replaceTracker(UUID id, GolemTracker data) {
		tracker.put(id, data);
	}

	public void init() {

	}

}
