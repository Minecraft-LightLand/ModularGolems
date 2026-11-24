package dev.xkmc.modulargolems.compat.curio;

import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.l2tabs.compat.BaseCuriosListMenu;
import dev.xkmc.l2tabs.compat.CuriosEventHandler;
import dev.xkmc.l2tabs.compat.CuriosWrapper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

/**
 * 背包物品版的饰品界面：dummy 只存在于菜单内部，任何改动都要实时写回物品。
 */
public class GolemItemCuriosListMenu extends BaseCuriosListMenu<GolemItemCuriosListMenu> {

	@Nullable
	public static GolemItemCuriosListMenu fromNetwork(MenuType<GolemItemCuriosListMenu> type, int wid, Inventory plInv, FriendlyByteBuf buf) {
		ItemStack stack = buf.readItem();
		int slot = buf.readInt();
		int page = buf.readInt();
		var level = Proxy.getClientWorld();
		if (level == null) return null;
		AbstractGolemEntity<?, ?> dummy = GolemItemCuriosMenuPvd.createDummy(stack, level);
		if (dummy == null) return null;
		return new GolemItemCuriosListMenu(type, wid, plInv, stack, slot, new CuriosWrapper(dummy, page));
	}

	public final ItemStack sourceStack;
	public final int sourceSlot;

	protected GolemItemCuriosListMenu(MenuType<?> type, int wid, Inventory plInv, ItemStack stack, int slot, CuriosWrapper curios) {
		super(type, wid, plInv, curios);
		this.sourceStack = stack;
		this.sourceSlot = slot;
	}

	/** 把 dummy 当前状态写回背包里那件物品。 */
	private void saveSource() {
		if (inventory.player.level().isClientSide) return;
		if (sourceSlot < 0 || sourceSlot >= inventory.getContainerSize()) return;
		ItemStack actual = inventory.getItem(sourceSlot);
		if (!(actual.getItem() instanceof GolemHolder<?, ?>)) return;
		if (!(curios.entity instanceof AbstractGolemEntity<?, ?> golem)) return;
		@SuppressWarnings({"rawtypes", "unchecked"})
		ItemStack ans = GolemHolder.setEntity((AbstractGolemEntity) golem);
		CompoundTag src = ans.getTag();
		if (src == null) return;
		CompoundTag dst = actual.getOrCreateTag();
		for (String key : src.getAllKeys()) dst.put(key, src.get(key).copy());
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
		if (sourceSlot >= 0 && sourceSlot < inventory.getContainerSize()) {
			ItemStack actual = inventory.getItem(sourceSlot);
			if (!(actual.getItem() instanceof GolemHolder<?, ?>)) {
				inventory.player.closeContainer();
				return;
			}
		}
		saveSource();
	}

	@Override
	public void slotsChanged(Container container) {
		super.slotsChanged(container);
		saveSource();
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		saveSource();
	}

	@Override
	public void switchPage(ServerPlayer player, int i) {
		var pvd = new GolemItemCuriosMenuPvd(sourceStack.copy(), sourceSlot, i);
		CuriosEventHandler.openMenuWrapped(player, () -> NetworkHooks.openScreen(player, pvd, pvd::writeBuffer));
	}

	@Override
	public boolean stillValid(Player player) {
		return player.isAlive();
	}

}
