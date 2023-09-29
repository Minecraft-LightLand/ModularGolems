package dev.xkmc.modulargolems.content.menu.config;

import dev.xkmc.l2library.base.menu.base.BaseContainerMenu;
import dev.xkmc.l2library.base.menu.base.SpriteManager;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Function;

public class BaseGolemConfigMenu<T extends BaseGolemConfigMenu<T>> extends BaseContainerMenu<T> {

	protected BaseGolemConfigMenu(MenuType<?> type, int wid, Inventory plInv, SpriteManager manager, Function<T, SimpleContainer> factory) {
		super(type, wid, plInv, manager, factory, true);
	}


}
