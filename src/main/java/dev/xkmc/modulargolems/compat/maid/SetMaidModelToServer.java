package dev.xkmc.modulargolems.compat.maid;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsMenu;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class SetMaidModelToServer extends SerialPacketBase {

	@SerialClass.SerialField
	public int id;
	@SerialClass.SerialField
	public String modelId;
	@SerialClass.SerialField
	public String soundPackId;
	public static SetMaidModelToServer of(int id, String modelId, String soundPackId) {
		var ans = new SetMaidModelToServer();
		ans.id = id;
		ans.modelId = modelId;
		ans.soundPackId = soundPackId;
		return ans;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		var sp = context.getSender();
		if (sp == null) return;
		// 由背包物品打开的装备界面：改的是临时 dummy，直接写回物品
		if (sp.containerMenu instanceof EquipmentsMenu menu && menu.isItemMode()) {
			if (menu.golem instanceof HumanoidGolemEntity golem) {
				if (modelId != null) golem.setMaidModelId(modelId);
				if (soundPackId != null) golem.setSoundPackId(soundPackId);
				menu.saveSourceNow();
			}
			return;
		}
		var entity = sp.level().getEntity(id);
		if (entity instanceof HumanoidGolemEntity golem && golem.canModify(sp)) {
			if (modelId != null) golem.setMaidModelId(modelId);
			if (soundPackId != null) golem.setSoundPackId(soundPackId);
		}
	}

}
