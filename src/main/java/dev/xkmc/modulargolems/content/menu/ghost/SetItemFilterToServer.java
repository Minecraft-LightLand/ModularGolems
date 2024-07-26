package dev.xkmc.modulargolems.content.menu.ghost;

import dev.xkmc.l2serial.network.SerialPacketBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record SetItemFilterToServer(
		int slot, ItemStack stack
) implements SerialPacketBase<SetItemFilterToServer> {

	@Override
	public void handle(Player player) {
		if (player.containerMenu instanceof GhostItemMenu menu) {
			menu.setSlotContent(slot, stack);
		}
	}

}
