package dev.xkmc.modulargolems.content.menu.ghost;

import dev.xkmc.l2library.base.menu.base.MenuLayoutConfig;
import dev.xkmc.l2library.base.menu.base.PredSlot;
import dev.xkmc.l2library.base.menu.base.SpriteManager;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public abstract class GhostItemMenu extends AbstractContainerMenu {

	protected final Inventory inventory;
	public final MenuLayoutConfig sprite;
	protected final Container container;

	private int added = 0;

	protected GhostItemMenu(MenuType<?> type, int wid, Inventory plInv, SpriteManager manager, Container container) {
		super(type, wid);
		this.inventory = plInv;
		this.sprite = manager.get();
		this.container = container;

		int x = sprite.getPlInvX();
		int y = sprite.getPlInvY();
		this.bindPlayerInventory(plInv, x, y);
	}

	protected void bindPlayerInventory(Inventory plInv, int x, int y) {
		int k;
		for (k = 0; k < 3; ++k) {
			for (int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(plInv, j + k * 9 + 9, x + j * 18, y + k * 18));
			}
		}
		for (k = 0; k < 9; ++k) {
			this.addSlot(new Slot(plInv, k, x + k * 18, y + 58));
		}

	}

	protected void addSlot(String name, Predicate<ItemStack> pred) {
		this.sprite.getSlot(name, (x, y) -> new PredSlot(this.container, this.added++, x, y, pred), (n, i, j, s) -> addSlot(s));
	}

	protected abstract IGhostContainer getContainer(int slot);

	protected abstract int getSize();

	protected ItemStack getSlotContent(int slot) {
		return getContainer(slot).getItem(slot);
	}

	public void setSlotContent(int slot, ItemStack stack) {
		if (stack.isEmpty()) {
			removeContent(slot);
		} else if (getContainer(slot).getItem(slot).isEmpty()) {
			tryAddContent(slot, stack);
		} else {
			stack = stack.copy();
			stack.setCount(1);
			if (getContainer(slot).internalMatch(stack)) return;
			getContainer(slot).set(slot, stack);
		}
	}

	protected void tryAddContent(int slot, ItemStack stack) {
		if (getContainer(slot).size() < getSize()) {
			stack = stack.copy();
			stack.setCount(1);
			if (getContainer(slot).internalMatch(stack)) return;
			getContainer(slot).set(getContainer(slot).size(), stack);
		}
	}

	protected void removeContent(int slot) {
		if (slot < 0 || slot >= getContainer(slot).size())
			return;
		getContainer(slot).set(slot, ItemStack.EMPTY);
	}

	public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
		if (slotId < 36) {
			super.clicked(slotId, dragType, clickTypeIn, player);
		} else if (clickTypeIn != ClickType.THROW) {
			ItemStack held = getCarried();
			int slot = slotId - 36;
			ItemStack insert;
			if (clickTypeIn == ClickType.CLONE) {
				if (player.isCreative() && held.isEmpty()) {
					insert = getSlotContent(slot).copy();
					insert.setCount(insert.getMaxStackSize());
					setCarried(insert);
				}
			} else {
				if (held.isEmpty()) {
					insert = ItemStack.EMPTY;
				} else {
					insert = held.copy();
					insert.setCount(1);
				}
				setSlotContent(slot, insert);
			}
		}
	}

	@Override
	public boolean stillValid(Player player) {
		return player.isAlive();
	}

	public ItemStack quickMoveStack(Player playerIn, int index) {
		if (index < 36) {
			ItemStack stackToInsert = getSlot(index).getItem();
			tryAddContent(0, stackToInsert);
		} else {
			removeContent(index - 36);
		}
		return ItemStack.EMPTY;
	}

}
