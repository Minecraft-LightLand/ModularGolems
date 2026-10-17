package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.modulargolems.content.client.overlay.ClientSkillCD;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;

@SerialClass
public class GolemSkillCDToClient extends SerialPacketBase {

	@SerialClass.SerialField
	public int golem;
	@SerialClass.SerialField
	public ArrayList<Entry> data = new ArrayList<>();

	public GolemSkillCDToClient() {

	}

	@Override
	public void handle(NetworkEvent.Context context) {
		ClientSkillCD.handle(this);
	}

	public record Entry(String name, int cd, boolean translate) {
	}

}
