package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.l2serial.network.SerialPacketBase;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public record ConfigSyncToClient(
		UUID id,
		int color,
		GolemConfigEntry entry
) implements SerialPacketBase<ConfigSyncToClient> {

	public static ConfigSyncToClient of(GolemConfigEntry entry) {
		return new ConfigSyncToClient(entry.getID(), entry.getColor(), entry);
	}

	@Override
	public void handle(Player player) {
		ClientDataHandler.handleUpdate(entry.init(id, color));
	}

}
