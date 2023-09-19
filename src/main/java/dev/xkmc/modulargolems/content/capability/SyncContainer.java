package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class SyncContainer {

	private static final int LIFETIME = 100;

	public final Map<UUID, Long> players = new LinkedHashMap<>();

	private long lastUpdateTime = 0;

	public void serverUpdate(ServerLevel level) {
		long time = level.getGameTime();
		if (lastUpdateTime == time) return;
		players.entrySet().removeIf(e -> level.getServer().getPlayerList().getPlayer(e.getKey()) != null && e.getValue() + LIFETIME < time);
		lastUpdateTime = time;
	}

	public void heartBeat(ServerLevel level, UUID uuid) {
		players.put(uuid, level.getGameTime());
		serverUpdate(level);
	}

	public void sendToAllTracking(ServerLevel level, SerialPacketBase packet) {
		serverUpdate(level);
		for (var e : players.keySet()) {
			ServerPlayer player = level.getServer().getPlayerList().getPlayer(e);
			if (player != null) {
				ModularGolems.HANDLER.toClientPlayer(packet, player);
			}
		}
	}

}
