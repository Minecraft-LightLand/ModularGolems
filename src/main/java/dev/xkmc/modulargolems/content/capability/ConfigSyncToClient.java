package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

@SerialClass
public class ConfigSyncToClient extends SerialPacketBase {

	@SerialClass.SerialField
	public UUID id;
	@SerialClass.SerialField
	public int color;
	@SerialClass.SerialField
	public GolemConfigEntry entry;

	@Deprecated
	public ConfigSyncToClient() {

	}

	public ConfigSyncToClient(GolemConfigEntry entry) {
		this.entry = entry;
		this.id = entry.getID();
		this.color = entry.getColor();
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		ClientDataHandler.handleUpdate(entry.init(id, color));
	}

}
