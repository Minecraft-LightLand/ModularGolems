package dev.xkmc.modulargolems.content.client.overlay;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;

@SerialClass
public class GolemSkillCDToClient extends SerialPacketBase {

	@SerialClass.SerialField
	public int golem;
	@SerialClass.SerialField
	public boolean ride;
	@SerialClass.SerialField
	public ArrayList<Entry> data = new ArrayList<>();

	public GolemSkillCDToClient() {

	}

	@Override
	public void handle(NetworkEvent.Context context) {
		if (ride) ClientRideSkillCD.handle(this);
		else ClientSkillCD.handle(this);
	}

	/**
	 * @param group 该技能所属傀儡的标题（entity type 的翻译 key，空串表示不分组）
	 */
	public record Entry(String name, int cd, boolean translate, String group) {
		public Entry(String name, int cd, boolean translate) {
			this(name, cd, translate, "");
		}
	}

}
