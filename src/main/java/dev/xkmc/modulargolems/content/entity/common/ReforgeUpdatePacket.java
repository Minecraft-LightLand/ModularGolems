package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.modulargolems.events.ClientPacketHandler;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class ReforgeUpdatePacket extends SerialPacketBase {

	public static ReforgeUpdatePacket of(AbstractGolemEntity<?, ?> golem, int reforge) {
		var ans = new ReforgeUpdatePacket();
		ans.golem = golem.getId();
		ans.reforge = reforge;
		return ans;
	}

	@SerialClass.SerialField
	public int golem;
	@SerialClass.SerialField
	public int reforge;

	public ReforgeUpdatePacket() {

	}

	@Override
	public void handle(NetworkEvent.Context context) {
		ClientPacketHandler.handleReforge(golem, reforge);
	}

}
