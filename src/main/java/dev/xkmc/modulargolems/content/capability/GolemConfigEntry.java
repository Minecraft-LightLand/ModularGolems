package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@SerialClass
public class GolemConfigEntry {

	public static GolemConfigEntry getDefault(UUID id, int color, Component name) {
		return new GolemConfigEntry(name).init(id, color);
	}

	public final SyncContainer sync = new SyncContainer();

	private UUID id;
	private int color;

	@SerialField
	private Component name;
	@SerialField
	public int defaultMode;
	@SerialField
	public boolean summonToPosition;
	@SerialField
	public boolean locked;

	@SerialField
	public PickupFilterConfig pickupFilter = new PickupFilterConfig();
	@SerialField
	public TargetFilterConfig targetFilter = new TargetFilterConfig();
	@SerialField
	public SquadConfig squadConfig = new SquadConfig();
	@SerialField
	public PathConfig pathConfig = new PathConfig();

	@Deprecated
	public GolemConfigEntry() {

	}

	private GolemConfigEntry(Component comp) {
		name = comp;
		targetFilter.initDefault();
	}

	public Component getDisplayName() {
		if (name == null) {
			name = Component.literal("Unnamed");
		}
		return name;
	}

	public GolemConfigEntry init(UUID id, int color) {
		this.id = id;
		this.color = color;
		return this;
	}

	public void heartBeat(ServerLevel level, ServerPlayer player) {
		if (sync.heartBeat(level, player.getUUID())) {
			ModularGolems.HANDLER.toClientPlayer(ConfigSyncToClient.of(this), player);
		}
	}

	public UUID getID() {
		return id;
	}

	public int getColor() {
		return color;
	}

	public void clientTick(Level level, boolean updated) {
		sync.clientTick(this, level, updated);
	}

	public void sync(Level level) {
		if (level instanceof ServerLevel sl) {
			sync.sendToAllTracking(sl, ConfigSyncToClient.of(this));
		} else {
			ModularGolems.HANDLER.toServer(ConfigUpdateToServer.of(level, this));
		}
	}

	public GolemConfigEntry copyFrom(@Nullable GolemConfigEntry entry) {
		if (entry != null)
			sync.clientReplace(entry.sync);
		return this;
	}

	public void setName(Component hoverName, ServerLevel level) {
		name = hoverName;
		sync(level);
	}

}
