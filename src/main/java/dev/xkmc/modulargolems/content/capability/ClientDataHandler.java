package dev.xkmc.modulargolems.content.capability;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

import java.util.UUID;

public class ClientDataHandler {

	public static void handleUpdate(GolemConfigEntry init) {
		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) return;
		GolemConfigStorage storage = GolemConfigStorage.get(level);
		storage.replaceStorage(init);
	}

	public static void handleTracked(UUID id, GolemTracker data) {
		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) return;
		GolemConfigStorage storage = GolemConfigStorage.get(level);
		storage.replaceTracker(id, data);
	}

}
