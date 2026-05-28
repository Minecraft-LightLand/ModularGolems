package dev.xkmc.modulargolems.content.entity.humanoid.skin;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class SetPlayerSkinToServer extends SerialPacketBase {

	@SerialClass.SerialField
	public int id;
	@SerialClass.SerialField
	public String skin;

	public static SetPlayerSkinToServer of(int id, String skin) {
		var ans = new SetPlayerSkinToServer();
		ans.id = id;
		ans.skin = skin;
		return ans;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		var sp = context.getSender();
		if (sp == null) return;
		var entity = sp.level().getEntity(id);
		if (entity instanceof HumanoidGolemEntity golem && golem.canModify(sp)) {
			golem.setPlayerSkin(skin);
		}
	}

}
