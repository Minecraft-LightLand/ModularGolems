package dev.xkmc.modulargolems.content.client.overlay;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemSkillCDRequest;
import dev.xkmc.modulargolems.content.entity.common.GolemSkillCDToClient;
import dev.xkmc.modulargolems.init.ModularGolems;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientSkillCD {

	private static final Map<Integer, List<GolemSkillCDToClient.Entry>> CACHE = new HashMap<>();
	private static int lastId = -1;
	private static long lastTime = Long.MIN_VALUE;

	public static void handle(GolemSkillCDToClient packet) {
		CACHE.put(packet.golem, packet.data);
	}

	public static List<GolemSkillCDToClient.Entry> get(int id) {
		return CACHE.getOrDefault(id, List.of());
	}

	public static void request(AbstractGolemEntity<?, ?> golem) {
		long time = golem.level().getGameTime();
		if (golem.getId() == lastId && time - lastTime < 10) return;
		lastId = golem.getId();
		lastTime = time;
		ModularGolems.HANDLER.toServer(GolemSkillCDRequest.of(golem.getId()));
	}

}
