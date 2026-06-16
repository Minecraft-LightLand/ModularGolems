package dev.xkmc.modulargolems.content.entity.humanoid.skin;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraft.world.entity.player.Player;

public record SetPlayerSkinToServer(int id, String skin) implements SerialPacketBase<SetPlayerSkinToServer> {

	public static SetPlayerSkinToServer of(int id, String skin) {
		return new SetPlayerSkinToServer(id, skin);
	}

	@Override
	public void handle(Player sp) {
		var entity = sp.level().getEntity(id);
		if (entity instanceof HumanoidGolemEntity golem && golem.canModify(sp)) {
			golem.setPlayerSkin(skin);
		}
	}

}
