package dev.xkmc.modulargolems.content.menu.equipment;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

@SerialClass
public class OpenItemEquipmentMenuToServer extends SerialPacketBase {

	@SerialClass.SerialField
	public ItemStack stack = ItemStack.EMPTY;
	// 客户端所在玩家背包槽位。优先按槽位打开，避免带傀儡组 NBT 的物品因 NBT 比对不相等而打不开。
	@SerialClass.SerialField
	public int slot = -1;

	public static OpenItemEquipmentMenuToServer of(ItemStack stack, int slot) {
		var ans = new OpenItemEquipmentMenuToServer();
		ans.stack = stack;
		ans.slot = slot;
		return ans;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		var player = context.getSender();
		if (player == null) return;
		var inv = player.getInventory();
		// 先按槽位打开（最可靠；不依赖 NBT 完全相等）
		if (slot >= 0 && slot < inv.getContainerSize()) {
			ItemStack found = inv.getItem(slot);
			if (found.getItem() instanceof GolemHolder<?, ?>) {
				ItemEquipmentsMenuProvider pvd = new ItemEquipmentsMenuProvider(found, inv, slot);
				NetworkHooks.openScreen(player, pvd, pvd::writeBuffer);
				return;
			}
		}
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack found = inv.getItem(i);
			if (!(found.getItem() instanceof GolemHolder<?, ?>)) continue;
			if (!ItemStack.matches(found, stack)) continue;
			ItemEquipmentsMenuProvider pvd = new ItemEquipmentsMenuProvider(found, inv, i);
			NetworkHooks.openScreen(player, pvd, pvd::writeBuffer);
			return;
		}
		// 只允许打开真实存在于玩家背包里的物品；创造模式搜索页等没有实际槽位的物品一律不打开。
	}

}
