package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

@SerialClass
public class GolemCommandEntry {

	public static GolemCommandEntry getDefault(UUID id, int color, Component name) {
		return new GolemCommandEntry(name).init(id, color);
	}

	private final SyncContainer sync = new SyncContainer();

	private UUID id;
	private int color;
	private Component nameComp;

	@SerialClass.SerialField
	private String name;


	@Deprecated
	public GolemCommandEntry() {

	}

	public GolemCommandEntry(Component comp) {
		nameComp = comp;
		name = Component.Serializer.toJson(comp);
	}

	public Component getDisplayName() {
		if (nameComp == null) {
			nameComp = Component.Serializer.fromJson(name);
		}
		if (nameComp == null) {
			nameComp = Component.literal("Unnamed");
		}
		return nameComp;
	}

	public GolemCommandEntry init(UUID id, int color) {
		this.id = id;
		this.color = color;
		return this;
	}

	public void heartBeat(ServerLevel level, ServerPlayer player) {
		sync.heartBeat(level, player.getUUID());
	}

	public UUID getID() {
		return id;
	}

	public int getColor() {
		return color;
	}

}
