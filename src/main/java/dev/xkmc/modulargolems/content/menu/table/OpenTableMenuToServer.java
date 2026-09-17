package dev.xkmc.modulargolems.content.menu.table;

import dev.xkmc.l2library.base.menu.base.PredSlot;
import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemMiscs;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

@SerialClass
public class OpenTableMenuToServer extends SerialPacketBase {

	@SerialClass.SerialField
	public TableTabType type;

	@SerialClass.SerialField
	public boolean simple;

	@Deprecated
	public OpenTableMenuToServer() {

	}

	public OpenTableMenuToServer(TableTabType type, boolean simple) {
		this.type = type;
		this.simple = simple;
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
boolean simple = this.simple || menu instanceof SimpleAssembleMenu
			|| menu instanceof SimpleDisintegrateMenu
			|| menu instanceof SimpleUpgradeMenu;
		if (simple && type.ordinal() < TableTabType.CRAFT.ordinal()) {
			NetworkHooks.openScreen(player, switch (type) {
				case ASSEMBLE -> provider(GolemMiscs.SIMPLE_ASSEMBLE.get());
				case DISINTEGRATE -> provider(GolemMiscs.SIMPLE_DISINTEGRATE.get());
				case UPGRADE -> provider(GolemMiscs.SIMPLE_UPGRADE.get());
				default -> throw new IllegalArgumentException("unreachable");
			});
		} else {
			NetworkHooks.openScreen(player, type);
		}
		menu = player.containerMenu;
		menu.setCarried(stack);
		if (!golem.isEmpty()) {
			if (menu instanceof ITableMenu table) {
				var target = table.getMainSlot();
				if (target instanceof PredSlot ps && !ps.mayPlace(golem)) {
					player.getInventory().placeItemBackInInventory(golem);
				} else {
					table.getMainSlot().set(golem);
				}
			} else {
				player.getInventory().placeItemBackInInventory(golem);
			}
		}
	}

	private MenuProvider provider(net.minecraft.world.inventory.MenuType<?> mt) {
		return new SimpleMenuProvider((wid, inv, p) -> {
			if (mt == GolemMiscs.SIMPLE_ASSEMBLE.get())
				return new SimpleAssembleMenu(mt, wid, inv);
			if (mt == GolemMiscs.SIMPLE_DISINTEGRATE.get())
				return new SimpleDisintegrateMenu(mt, wid, inv);
			return new SimpleUpgradeMenu(mt, wid, inv);
		}, mt == GolemMiscs.SIMPLE_DISINTEGRATE.get()
				? MGLangData.TAB_DISINTEGRATE_SIMPLE.get() : type.getDisplayName());
	}

}
