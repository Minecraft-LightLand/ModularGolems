package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.multiplayer.ClientLevel;

public class ClientDataHandler {

	public static void handleUpdate(GolemCommandEntry init) {
		ClientLevel level = Proxy.getClientWorld();
		if (level == null) return;
		GolemCommandStorage storage = GolemCommandStorage.get(level);
		storage.replaceStorage(init);
	}

}
