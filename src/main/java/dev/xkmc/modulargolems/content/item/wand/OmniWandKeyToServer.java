package dev.xkmc.modulargolems.content.item.wand;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

/** Triggered on the server when a player presses an omnipotent wand function key. */
@SerialClass
public record OmniWandKeyToServer(int function) implements SerialPacketBase<OmniWandKeyToServer> {

	@Override
	public void handle(Player player) {
		if (player == null || player.level().isClientSide()) return;
		var func = OmniWandFunction.byOrdinal(function);
		if (func == null) return;
		InteractionHand hand = null;
		for (var candidate : InteractionHand.values()) {
			if (OmniWandFunction.of(player.getItemInHand(candidate)) != null) {
				hand = candidate;
				break;
			}
		}
		if (hand == null) return;
		func.item().useFromKey(player.level(), player, hand);
	}

}
