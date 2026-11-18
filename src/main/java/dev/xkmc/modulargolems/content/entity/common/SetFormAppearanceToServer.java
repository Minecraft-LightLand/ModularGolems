package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class SetFormAppearanceToServer extends SerialPacketBase {

	@SerialClass.SerialField
	public int id;
	@SerialClass.SerialField
	public int mode;

	public static SetFormAppearanceToServer of(int id, int mode) {
		var ans = new SetFormAppearanceToServer();
		ans.id = id;
		ans.mode = mode;
		return ans;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		var sp = context.getSender();
		if (sp == null) return;
		var entity = sp.level().getEntity(id);
		if (entity instanceof AbstractGolemEntity<?, ?> golem && golem.canModify(sp)) {
			golem.setFormAppearance(mode);
		}
	}

}
