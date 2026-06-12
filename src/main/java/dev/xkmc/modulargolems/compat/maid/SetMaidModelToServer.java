package dev.xkmc.modulargolems.compat.maid;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraft.world.entity.player.Player;

public record SetMaidModelToServer(int id, String modelId, String soundPackId)
		implements SerialPacketBase<SetMaidModelToServer> {

	public static SetMaidModelToServer of(int id, String modelId, String soundPackId) {
		return new SetMaidModelToServer(id, modelId, soundPackId);
	}

	@Override
	public void handle(Player player) {
		var entity = player.level().getEntity(id);
		if (entity instanceof HumanoidGolemEntity golem && golem.canModify(player)) {
			if (modelId != null) golem.setMaidModelId(modelId);
			if (soundPackId != null) golem.setSoundPackId(soundPackId);
		}
	}

}
