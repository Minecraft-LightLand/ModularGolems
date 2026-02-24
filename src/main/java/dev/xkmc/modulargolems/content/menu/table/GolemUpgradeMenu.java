package dev.xkmc.modulargolems.content.menu.table;

import dev.xkmc.l2core.base.menu.base.BaseContainerMenu;
import dev.xkmc.l2core.base.menu.base.SpriteManager;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class GolemUpgradeMenu extends BaseContainerMenu<GolemUpgradeMenu> implements ITableMenu {

	public static GolemUpgradeMenu fromNetwork(MenuType<GolemUpgradeMenu> type, int wid, Inventory plInv, FriendlyByteBuf buf) {
		return new GolemUpgradeMenu(type, wid, plInv);
	}

	public static final SpriteManager MANAGER = new SpriteManager(ModularGolems.MODID, "upgrades");

	protected final GolemUpgradeItemHandler handler;
	protected final DataSlot page, maxPage;

	public GolemUpgradeMenu(MenuType<?> type, int wid, Inventory plInv) {
		super(type, wid, plInv, MANAGER, e -> new BaseContainer<>(1, e), true);
		handler = new GolemUpgradeItemHandler(() -> getAsPredSlot("golem"), plInv.player.level().isClientSide());
		addSlot("golem", e -> e.getItem() instanceof GolemHolder<?, ?>);
		getLayout().getSlot("upgrades", (x, y) -> new UpgradeSlot(handler, -1 + added++, x, y), this::addSlot);
		page = addDataSlot(DataSlot.shared(handler.data, 0));
		maxPage = addDataSlot(DataSlot.shared(handler.data, 1));
	}

	@Override
	public Slot getMainSlot() {
		return getAsPredSlot("golem");
	}

	@Override
	public void slotsChanged(Container cont) {
		handler.setHolder(getAsPredSlot("golem").getItem());
		super.slotsChanged(cont);
	}

	@Override
	public boolean clickMenuButton(Player player, int val) {
		if (val == -1) {
			if (page.get() > 0) {
				page.set(page.get() - 1);
				return true;
			}
			return false;
		}
		if (val == 1) {
			if (page.get() < maxPage.get()) {
				page.set(page.get() + 1);
				return true;
			}
			return false;
		}
		return false;
	}

	@Override
	public ItemStack quickMoveStack(Player pl, int id) {
		ItemStack stack = slots.get(id).getItem();
		if (id >= 36) {
			if (slots.get(id) instanceof UpgradeSlot upSlot) {
				if (moveItemStackTo(stack.copyWithCount(1), 0, 36, true)) {
					slots.get(id).remove(1);
				}
			} else {
				moveItemStackTo(stack, 0, 36, true);
				slots.get(id).setChanged();
			}
		} else {
			if (!moveItemStackTo(stack, 36, 37, false)) {
				for (int i = 37; i < slots.size(); i++) {
					var slotStack = slots.get(i).getItem();
					if (slotStack.isEmpty() || ItemStack.isSameItemSameComponents(slotStack, stack)) {
						if (handler.insertItem(i - 37, stack.copyWithCount(1), false).isEmpty()) {
							stack.shrink(1);
							break;
						}
					}
				}
			}
		}
		container.setChanged();
		return ItemStack.EMPTY;
	}

	@Override
	protected boolean shouldClear(Container container, int slot) {
		return super.shouldClear(container, slot);
	}

}
