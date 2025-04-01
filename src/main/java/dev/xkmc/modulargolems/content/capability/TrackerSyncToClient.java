package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

@SerialClass
public class TrackerSyncToClient extends SerialPacketBase {

	@SerialClass.SerialField
	public UUID id;
	@SerialClass.SerialField
	public GolemTracker entry;

	@Deprecated
	public TrackerSyncToClient() {

	}

	public TrackerSyncToClient(UUID id, GolemTracker entry) {
		this.entry = entry;
		this.id = id;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		ClientDataHandler.handleTracked(id, entry);
	}

}
