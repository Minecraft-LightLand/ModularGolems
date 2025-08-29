package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.modulargolems.events.ClientPacketHandler;
import net.minecraft.world.entity.player.Player;

public record ReforgeUpdatePacket(int golem, int reforge) implements SerialPacketBase<ReforgeUpdatePacket> {

	public static ReforgeUpdatePacket of(AbstractGolemEntity<?, ?> golem, int reforge) {
		return new ReforgeUpdatePacket(golem.getId(), reforge);
	}

	@Override
	public void handle(Player player) {
		ClientPacketHandler.handleReforge(golem, reforge);
	}

}
