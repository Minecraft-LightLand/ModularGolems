package dev.xkmc.modulargolems.content.menu.ghost;

import dev.xkmc.l2library.base.menu.base.MenuLayoutConfig;
import dev.xkmc.l2library.base.menu.base.PredSlot;
import dev.xkmc.l2library.base.menu.base.SpriteManager;
import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.modulargolems.content.capability.GolemConfigEditor;
import dev.xkmc.modulargolems.content.capability.PickupFilterEditor;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class ItemConfigMenu extends AbstractContainerMenu {

	public static final SpriteManager MANAGER = new SpriteManager(ModularGolems.MODID, "config_pickup");
	public static final int SIZE = 18;

	public static ItemConfigMenu fromNetwork(MenuType<ItemConfigMenu> type, int wid, Inventory plInv, FriendlyByteBuf buf) {
		var id = buf.readUUID();
		int color = buf.readInt();
		var level = Proxy.getClientWorld();
		var editor = new GolemConfigEditor.Readable(level, id, color);
		return new ItemConfigMenu(type, wid, plInv, new SimpleContainer(SIZE), editor.getFilter());
	}

	protected final Inventory inventory;
	protected final MenuLayoutConfig sprite;
	protected final Container container;
	protected final PickupFilterEditor editor;

	private int added = 0;

	protected ItemConfigMenu(MenuType<?> type, int wid, Inventory plInv, Container container, PickupFilterEditor editor) {
		super(type, wid);
		this.inventory = plInv;
		this.sprite = MANAGER.get();
		this.container = container;
		this.editor = editor;

		int x = sprite.getPlInvX();
		int y = sprite.getPlInvY();
		this.bindPlayerInventory(plInv, x, y);

		addSlot("grid", e -> true);
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

	protected PickupFilterEditor getConfig() {
		return editor;
	}

	protected ItemStack getSlotContent(int slot) {
		return getConfig().getItem(slot);
	}

	public void setSlotContent(int slot, ItemStack stack) {
		if (stack.isEmpty()) {
			removeContent(slot);
		} else if (getConfig().getItem(slot).isEmpty()) {
			tryAddContent(stack);
		} else {
			stack = stack.copy();
			stack.setCount(1);
			if (getConfig().internalMatch(stack)) return;
			getConfig().set(slot, stack);
		}
	}

	protected void tryAddContent(ItemStack stack) {
		if (getConfig().size() < SIZE) {
			stack = stack.copy();
			stack.setCount(1);
			if (getConfig().internalMatch(stack)) return;
			getConfig().set(getConfig().size(), stack);
		}
	}

	protected void removeContent(int slot) {
		if (slot < 0 || slot >= getConfig().size())
			return;
		getConfig().set(slot, ItemStack.EMPTY);
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
			tryAddContent(stackToInsert);
		} else {
			removeContent(index - 36);
		}
		return ItemStack.EMPTY;
	}

}
