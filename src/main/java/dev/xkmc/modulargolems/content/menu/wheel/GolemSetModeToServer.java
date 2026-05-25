package dev.xkmc.modulargolems.content.menu.wheel;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.mode.GolemModes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public record GolemSetModeToServer(int golem, int mode) implements SerialPacketBase<GolemSetModeToServer> {

	public static GolemSetModeToServer of(AbstractGolemEntity<?, ?> golem, int mode) {
		return new GolemSetModeToServer(golem.getId(), mode);
	}

	@Override
	public void handle(Player player) {
		var e = player.level().getEntity(golem);
		if (!(e instanceof AbstractGolemEntity<?, ?> g)) return;
		if (mode < 0 || mode >= GolemModes.LIST.size()) return;
		var m = GolemModes.LIST.get(mode);
		if (!g.canWandModify(player)) return;
		g.setMode(mode, m.hasPos() ? g.blockPosition() : BlockPos.ZERO);
	}

}
