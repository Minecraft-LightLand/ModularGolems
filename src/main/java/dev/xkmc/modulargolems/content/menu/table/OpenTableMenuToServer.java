package dev.xkmc.modulargolems.content.menu.table;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

@SerialClass
public class OpenTableMenuToServer extends SerialPacketBase {

	@SerialClass.SerialField
	public TableTabType type;

	@Deprecated
	public OpenTableMenuToServer() {

	}

	public OpenTableMenuToServer(TableTabType type) {
		this.type = type;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		var player = context.getSender();
		if (player == null) return;
		AbstractContainerMenu menu = player.containerMenu;
		ItemStack stack = menu.getCarried();
		menu.setCarried(ItemStack.EMPTY);
		ItemStack golem = ItemStack.EMPTY;
		if (menu instanceof ITableMenu table) {
			golem = table.getMainSlot().getItem();
			table.getMainSlot().set(ItemStack.EMPTY);
		}
		NetworkHooks.openScreen(player, type);
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
