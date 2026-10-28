package dev.xkmc.modulargolems.content.item.wand;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;

/**
 * Triggered on the server when a player presses an omnipotent wand function key. The matching omnipotent wand's
 * use() is invoked, so its client/server branches and its isShiftKeyDown() check all behave exactly as they do for
 * a real right click.
 */
@SerialClass
public class OmniWandKeyToServer extends SerialPacketBase {

	@SerialClass.SerialField
	public int function;

	public OmniWandKeyToServer() {
	}

	public OmniWandKeyToServer(OmniWandFunction function) {
		this.function = function.ordinal();
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		var player = context.getSender();
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
