package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public record TrackerHeartBeatToServer(UUID id) implements SerialPacketBase<TrackerHeartBeatToServer> {

	@Override
	public void handle(Player player) {
		if (!(player instanceof ServerPlayer sp)) return;
		var entry = GolemConfigStorage.get(player.level()).getTracker(id);
		ModularGolems.HANDLER.toClientPlayer(new TrackerSyncToClient(player.getUUID(), entry), sp);
	}

}
