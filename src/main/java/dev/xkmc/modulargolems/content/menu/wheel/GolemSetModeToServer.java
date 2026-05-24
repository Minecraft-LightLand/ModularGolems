package dev.xkmc.modulargolems.content.menu.wheel;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.mode.GolemModes;
import net.minecraft.core.BlockPos;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class GolemSetModeToServer extends SerialPacketBase {

	public static GolemSetModeToServer of(AbstractGolemEntity<?, ?> golem, int mode) {
		var ans = new GolemSetModeToServer();
		ans.golem = golem.getId();
		ans.mode = mode;
		return ans;
	}

	@SerialClass.SerialField
	public int golem, mode;

	@Override
	public void handle(NetworkEvent.Context context) {
		var player = context.getSender();
		if (player == null) return;
		var e = player.level().getEntity(golem);
		if (!(e instanceof AbstractGolemEntity<?, ?> golem)) return;
		if (mode < 0 || mode >= GolemModes.LIST.size()) return;
		var m = GolemModes.LIST.get(mode);
		if (!golem.canWandModify(player)) return;
		golem.setMode(mode, m.hasPos() ? golem.blockPosition() : BlockPos.ZERO);
	}

}
