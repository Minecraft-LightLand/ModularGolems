package dev.xkmc.modulargolems.content.menu.table;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;

/**
 * Carries the name typed into the assembly screen over to the server, which is what actually stamps it onto
 * the crafted golem item.
 * <p>
 * The length and character limits of the text box only exist on the client, so they are re-checked here:
 * the name ends up in the item's NBT and travels to every client that sees the item.
 */
@SerialClass
public class SetGolemNameToServer extends SerialPacketBase {

	private static final int MAX_LENGTH = 32;

	@SerialClass.SerialField
	public String name;

	@Deprecated
	public SetGolemNameToServer() {
	}

	public SetGolemNameToServer(String name) {
		this.name = name;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		var player = context.getSender();
		if (player == null || !(player.containerMenu instanceof GolemAssembleMenu menu)) return;
		menu.setGolemName(sanitize(name));
	}

	@Nullable
	private static String sanitize(@Nullable String raw) {
		if (raw == null) return null;
		String cleaned = raw.replace('\u0000', ' ').replace('\u00A7', ' ');
		return cleaned.length() <= MAX_LENGTH ? cleaned : cleaned.substring(0, MAX_LENGTH);
	}

}
