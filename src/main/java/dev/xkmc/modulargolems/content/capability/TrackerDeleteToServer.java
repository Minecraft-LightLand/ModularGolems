package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

@SerialClass
public class TrackerDeleteToServer extends SerialPacketBase {

	@SerialClass.SerialField
	public UUID owner, golem;

	@Deprecated
	public TrackerDeleteToServer() {

	}

	public TrackerDeleteToServer(UUID owner, UUID golem) {
		this.owner = owner;
		this.golem = golem;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		var sp = context.getSender();
		if (sp == null || !sp.getUUID().equals(owner)) return;
		var tracker = GolemConfigStorage.get(sp.level()).getTracker(owner);
		tracker.data.remove(golem);
		ModularGolems.HANDLER.toClientPlayer(new TrackerSyncToClient(owner, tracker), sp);
	}

}
