package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public record TrackerDeleteToServer(UUID owner, UUID golem)
		implements SerialPacketBase<TrackerDeleteToServer> {

	@Override
	public void handle(Player player) {
		if (!(player instanceof ServerPlayer sp)) return;
		if (!sp.getUUID().equals(owner)) return;
		var tracker = GolemConfigStorage.get(sp.level()).getTracker(owner);
		tracker.data.remove(golem);
		ModularGolems.HANDLER.toClientPlayer(new TrackerSyncToClient(owner, tracker), sp);
	}

}
