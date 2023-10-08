package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

@SerialClass
public class ConfigHeartBeatToServer extends SerialPacketBase {

	@SerialClass.SerialField
	public UUID id;

	@SerialClass.SerialField
	public int color;

	@Deprecated
	public ConfigHeartBeatToServer() {

	}

	public ConfigHeartBeatToServer(UUID id, int color) {
		this.id = id;
		this.color = color;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		ServerPlayer player = context.getSender();
		if (player == null) return;
		var entry = GolemConfigStorage.get(player.level()).getStorage(id, color);
		if (entry == null) return;
		entry.heartBeat(player.serverLevel(), player);
	}

}
