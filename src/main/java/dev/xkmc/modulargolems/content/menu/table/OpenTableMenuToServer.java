package dev.xkmc.modulargolems.content.menu.table;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemMiscs;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

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
		boolean simple = menu instanceof SimpleAssembleMenu
				|| menu instanceof SimpleDisintegrateMenu
				|| menu instanceof SimpleUpgradeMenu;
		if (simple && type.ordinal() < TableTabType.CRAFT.ordinal()) {
			player.openMenu(simpleProvider());
		} else {
			player.openMenu(type);
		}
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

	private MenuProvider simpleProvider() {
		return new SimpleMenuProvider((wid, inv, p) -> switch (type) {
			case ASSEMBLE -> new SimpleAssembleMenu(GolemMiscs.SIMPLE_ASSEMBLE.get(), wid, inv);
			case DISINTEGRATE -> new SimpleDisintegrateMenu(GolemMiscs.SIMPLE_DISINTEGRATE.get(), wid, inv);
			default -> new SimpleUpgradeMenu(GolemMiscs.SIMPLE_UPGRADE.get(), wid, inv);
		}, type == TableTabType.DISINTEGRATE ? MGLangData.TAB_DISINTEGRATE_SIMPLE.get() : type.getDisplayName());
	}

}
