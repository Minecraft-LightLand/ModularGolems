package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

@SerialClass
public class TrackerHeartBeatToServer extends SerialPacketBase {

	@SerialClass.SerialField
	public UUID id;

	@Deprecated
	public TrackerHeartBeatToServer() {

	}

	public TrackerHeartBeatToServer(UUID id) {
		this.id = id;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		ServerPlayer player = context.getSender();
		if (player == null) return;
		var entry = GolemConfigStorage.get(player.level()).getTracker(id);
		ModularGolems.HANDLER.toClientPlayer(new TrackerSyncToClient(player.getUUID(), entry), player);
	}

}
