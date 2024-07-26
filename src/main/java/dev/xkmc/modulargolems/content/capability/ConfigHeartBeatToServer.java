package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.l2serial.network.SerialPacketBase;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public record ConfigHeartBeatToServer(
		UUID id, int color
) implements SerialPacketBase<ConfigHeartBeatToServer> {

	@Override
	public void handle(Player player) {
		if (!(player instanceof ServerPlayer sp)) return;
		var entry = GolemConfigStorage.get(player.level()).getStorage(id, color);
		if (entry == null) return;
		entry.heartBeat(sp.serverLevel(), sp);
	}

}
