package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.l2serial.network.SerialPacketBase;
import net.minecraft.world.entity.player.Player;

public record SetFormAppearanceToServer(int id, int mode) implements SerialPacketBase<SetFormAppearanceToServer> {

	public static SetFormAppearanceToServer of(int id, int mode) {
		return new SetFormAppearanceToServer(id, mode);
	}

	@Override
	public void handle(Player player) {
		var entity = player.level().getEntity(id);
		if (entity instanceof AbstractGolemEntity<?, ?> golem && golem.canModify(player)) {
			golem.setFormAppearance(mode);
		}
	}

}
