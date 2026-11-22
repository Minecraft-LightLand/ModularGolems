package dev.xkmc.modulargolems.content.menu.table;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/** Carries the name typed into the assembly screen to the server. */
@SerialClass
public record SetGolemNameToServer(String name) implements SerialPacketBase<SetGolemNameToServer> {

	private static final int MAX_LENGTH = 32;

	@Override
	public void handle(Player player) {
		if (!(player instanceof ServerPlayer sp)) return;
		if (!(sp.containerMenu instanceof GolemAssembleMenu menu)) return;
		menu.setGolemName(sanitize(name));
	}

	private static String sanitize(String raw) {
		if (raw == null) return null;
		String cleaned = raw.replace('\u0000', ' ').replace('\u00A7', ' ');
		return cleaned.length() <= MAX_LENGTH ? cleaned : cleaned.substring(0, MAX_LENGTH);
	}

}
