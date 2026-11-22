package dev.xkmc.modulargolems.content.client.overlay;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

@SerialClass
public record GolemSkillCDRequest(int golem) implements SerialPacketBase<GolemSkillCDRequest> {

	public static GolemSkillCDRequest of(int id) {
		return new GolemSkillCDRequest(id);
	}

	@Override
	public void handle(Player player) {
		if (!(player instanceof ServerPlayer sp)) return;
		var e = sp.level().getEntity(golem);
		if (!(e instanceof AbstractGolemEntity<?, ?> g)) return;
		var packet = new GolemSkillCDToClient(g.getId(), GolemSkillCDHelper.collect(g));
		ModularGolems.HANDLER.toClientPlayer(packet, sp);
	}

}
