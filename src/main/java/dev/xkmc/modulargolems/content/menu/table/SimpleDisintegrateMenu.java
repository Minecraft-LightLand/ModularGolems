package dev.xkmc.modulargolems.content.menu.table;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class SimpleDisintegrateMenu extends GolemDisintegrateMenu {

	public SimpleDisintegrateMenu(MenuType<?> type, int wid, Inventory plInv) {
		super(type, wid, plInv);
		simpleMode = true;
	}

}