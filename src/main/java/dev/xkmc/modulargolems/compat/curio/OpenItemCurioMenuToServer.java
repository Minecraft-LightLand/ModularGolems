package dev.xkmc.modulargolems.compat.curio;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

/**
 * 由背包里的傀儡物品打开饰品界面。物品没有实体，服务端按物品数据造 dummy，界面里读写的饰品都写回物品。
 */
@SerialClass
public class OpenItemCurioMenuToServer extends SerialPacketBase {

	@SerialClass.SerialField
	public ItemStack stack = ItemStack.EMPTY;
	@SerialClass.SerialField
	public int slot = -1;

	public static OpenItemCurioMenuToServer of(ItemStack stack, int slot) {
		var ans = new OpenItemCurioMenuToServer();
		ans.stack = stack;
		ans.slot = slot;
		return ans;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		var player = context.getSender();
		if (player == null) return;
		var inv = player.getInventory();
		ItemStack found = ItemStack.EMPTY;
		int foundSlot = -1;
		// 优先按槽位找（不依赖 NBT 完全相等）
		if (slot >= 0 && slot < inv.getContainerSize()) {
			ItemStack cur = inv.getItem(slot);
			if (cur.getItem() instanceof GolemHolder<?, ?>) {
				found = cur;
				foundSlot = slot;
			}
		}
		if (found.isEmpty()) {
			for (int i = 0; i < inv.getContainerSize(); i++) {
				ItemStack cur = inv.getItem(i);
				if (!(cur.getItem() instanceof GolemHolder<?, ?>)) continue;
				if (!ItemStack.matches(cur, stack)) continue;
				found = cur;
				foundSlot = i;
				break;
			}
		}
		if (found.isEmpty()) return;
		// dummy 造不出来时不要发界面包，否则 createMenu 返回 null 会在服务端 NPE
		if (GolemItemCuriosMenuPvd.createDummy(found, player.level()) == null) return;
		var pvd = new GolemItemCuriosMenuPvd(found, foundSlot, 0);
		NetworkHooks.openScreen(player, pvd, pvd::writeBuffer);
	}

}
