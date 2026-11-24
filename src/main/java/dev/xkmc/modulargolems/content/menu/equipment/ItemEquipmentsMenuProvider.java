package dev.xkmc.modulargolems.content.menu.equipment;

import dev.xkmc.modulargolems.content.menu.registry.IMenuPvd;
import dev.xkmc.modulargolems.init.registrate.GolemMiscs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public record ItemEquipmentsMenuProvider(ItemStack stack, Container container, int slot) implements IMenuPvd {

	@Override
	public void writeBuffer(FriendlyByteBuf buf) {
		buf.writeItem(stack);
		// 槽位也要发到客户端：物品界面里的标签页（数值/饰品）切回来时要靠它重新定位那件物品，
		// 只发物品会因为大 NBT 在服务端比对不相等而找不到。
		buf.writeInt(slot);
	}

	@Override
	public Component getDisplayName() {
		return stack.getHoverName();
	}

	@Override
	public AbstractContainerMenu createMenu(int wid, Inventory inv, Player player) {
		return EquipmentsMenu.createItemMenu(GolemMiscs.ITEM_EQUIPMENTS.get(), wid, inv, stack, container, slot);
	}

}
