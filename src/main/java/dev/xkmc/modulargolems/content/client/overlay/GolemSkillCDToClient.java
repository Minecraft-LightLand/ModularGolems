package dev.xkmc.modulargolems.content.client.overlay;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;

@SerialClass
public record GolemSkillCDToClient(int golem, ArrayList<Entry> data) implements SerialPacketBase<GolemSkillCDToClient> {

	@Override
	public void handle(Player player) {
		ClientSkillCD.handle(this);
	}

	@SerialClass
	public record Entry(String name, int cd, boolean translate) {
	}

}
