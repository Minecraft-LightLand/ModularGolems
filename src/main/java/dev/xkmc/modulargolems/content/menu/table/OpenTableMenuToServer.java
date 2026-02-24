package dev.xkmc.modulargolems.content.menu.table;

import dev.xkmc.l2serial.network.SerialPacketBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public record OpenTableMenuToServer(
		TableTabType type
) implements SerialPacketBase<OpenTableMenuToServer> {

	@Override
	public void handle(Player player) {
		AbstractContainerMenu menu = player.containerMenu;
		ItemStack stack = menu.getCarried();
		menu.setCarried(ItemStack.EMPTY);
		ItemStack golem = ItemStack.EMPTY;
		if (menu instanceof ITableMenu table) {
			golem = table.getMainSlot().getItem();
			table.getMainSlot().set(ItemStack.EMPTY);
		}
		player.openMenu(type);
		menu = player.containerMenu;
		menu.setCarried(stack);
		if (!golem.isEmpty()) {
			if (menu instanceof ITableMenu table) {
				table.getMainSlot().set(golem);
			} else {
				player.getInventory().placeItemBackInInventory(golem);
			}
		}
	}

}
