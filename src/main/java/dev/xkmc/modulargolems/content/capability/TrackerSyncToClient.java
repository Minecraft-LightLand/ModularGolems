package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.l2serial.network.SerialPacketBase;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public record TrackerSyncToClient(UUID id, GolemTracker entry)
		implements SerialPacketBase<TrackerSyncToClient> {

	@Override
	public void handle(Player player) {
		ClientDataHandler.handleTracked(id, entry);
	}

}
