package dev.xkmc.modulargolems.content.menu.table;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class SimpleUpgradeMenu extends GolemUpgradeMenu {

	public SimpleUpgradeMenu(MenuType<?> type, int wid, Inventory plInv) {
		super(type, wid, plInv);
		handler.allowExtract = false;
	}

	@Override
	public ItemStack quickMoveStack(Player pl, int id) {
		if (id >= 36 && slots.get(id) instanceof UpgradeSlot) {
			return ItemStack.EMPTY;
		}
		return super.quickMoveStack(pl, id);
	}

}