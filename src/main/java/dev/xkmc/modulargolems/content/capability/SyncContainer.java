package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class SyncContainer {

	private static final int LIFETIME = 200, HEARTBEAT = 100;

	public final Map<UUID, Long> players = new LinkedHashMap<>();

	private long lastUpdateTime = 0;

	public void serverUpdate(ServerLevel level) {
		long time = level.getGameTime();
		if (lastUpdateTime == time) return;
		players.entrySet().removeIf(e -> level.getServer().getPlayerList().getPlayer(e.getKey()) == null || e.getValue() + LIFETIME < time);
		lastUpdateTime = time;
	}

	public boolean heartBeat(ServerLevel level, UUID uuid) {
		serverUpdate(level);
		return players.put(uuid, level.getGameTime()) == null;
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

	private long tick = 0;

	public void clientTick(GolemConfigEntry entry, Level level, boolean updated) {
		long current = level.getGameTime();
		if (updated) {
			tick = current;
		} else if (current - tick >= HEARTBEAT) {
			ModularGolems.HANDLER.toServer(new ConfigHeartBeatToServer(entry.getID(), entry.getColor()));
			tick = current;
		}
	}

	public void clientReplace(SyncContainer sync) {
		this.tick = sync.tick;
	}
}
