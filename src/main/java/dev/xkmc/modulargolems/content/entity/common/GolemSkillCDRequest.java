package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class GolemSkillCDRequest extends SerialPacketBase {

	@SerialClass.SerialField
	public int golem;

	public GolemSkillCDRequest() {

	}

	public static GolemSkillCDRequest of(int id) {
		var ans = new GolemSkillCDRequest();
		ans.golem = id;
		return ans;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		var player = context.getSender();
		if (player == null) return;
		var e = player.level().getEntity(golem);
		if (!(e instanceof AbstractGolemEntity<?, ?> golem)) return;
		var packet = new GolemSkillCDToClient();
		packet.golem = golem.getId();
		packet.data = GolemSkillCDHelper.collect(golem);
		ModularGolems.HANDLER.toClientPlayer(packet, player);
	}

}
