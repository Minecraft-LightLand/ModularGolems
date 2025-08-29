package dev.xkmc.modulargolems.events;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.client.Minecraft;

public class ClientPacketHandler {

	public static void handleReforge(int id, int reforge) {
		var level = Minecraft.getInstance().level;
		if (level == null) return;
		var e = level.getEntity(id);
		if (!(e instanceof AbstractGolemEntity<?, ?> golem)) return;
		golem.updateReforge(reforge);
	}

}
