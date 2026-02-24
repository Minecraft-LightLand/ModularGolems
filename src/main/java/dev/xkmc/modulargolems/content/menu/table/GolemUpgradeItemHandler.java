package dev.xkmc.modulargolems.content.menu.table;

import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.upgrade.IUpgradeItem;
import dev.xkmc.modulargolems.content.item.upgrade.UpgradeItem;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.function.Supplier;

public class GolemUpgradeItemHandler implements IItemHandlerModifiable {

	private static final int SIZE = 27;

	private final boolean client;
	private final Supplier<Slot> parent;

	public ItemStack golem = ItemStack.EMPTY;
	public int[] data = new int[]{0, 1};
	public int modification = 0;
	private GolemHolder<?, ?> holderItem = null;
	private ArrayList<GolemMaterial> materials = null;
	private ArrayList<IUpgradeItem> upgrades = null;
	private final LinkedHashMap<IUpgradeItem, ItemStack> upgradeMap = new LinkedHashMap<>();
	private ArrayList<ItemStack> upgradeList = new ArrayList<>();
	private Item lastForbidTest = Items.AIR;

	public GolemUpgradeItemHandler(Supplier<Slot> slot, boolean client) {
		this.client = client;
		parent = slot;
	}

	public void setHolder(ItemStack stack) {
		modification++;
		golem = stack;
		lastForbidTest = Items.AIR;
		if (!(golem.getItem() instanceof GolemHolder<?, ?> holder)) {
			holderItem = null;
			materials = null;
			upgrades = null;
			golem = ItemStack.EMPTY;
			upgradeMap.clear();
			upgradeList = new ArrayList<>();
			if (!client) {
				data[0] = 0;
				data[1] = 1;
			}
			return;
		}
		holderItem = holder;
		materials = GolemHolder.getMaterial(stack);
		upgrades = GolemHolder.getUpgrades(stack);
		upgradeMap.clear();
		for (var e : upgrades) {
			if (!upgradeMap.containsKey(e))
				upgradeMap.put(e, e.asItem().getDefaultInstance());
			else upgradeMap.get(e).grow(1);
		}
		upgradeList = new ArrayList<>(upgradeMap.values());
		if (!client) {
			data[1] = upgradeList.size() / SIZE + 1;
		}
	}

	public ItemStack appendUpgrade(UpgradeItem upgrade) {
		if (!upgrade.fitsOn(holderItem.getEntityType())) return ItemStack.EMPTY;
		var copy = new ArrayList<>(upgrades);
		copy.add(upgrade);
		int remaining = holderItem.getRemaining(materials, copy);
		if (remaining < 0) return ItemStack.EMPTY;
		var map = GolemMaterial.collectModifiers(materials, upgrades);
		for (var e : upgrade.get()) {
			if (map.getOrDefault(e.mod(), 0) >= e.mod().maxLevel) return ItemStack.EMPTY;
		}
		ItemStack result = golem.copy();
		GolemHolder.addUpgrade(result, upgrade);
		return result;
	}

	public ItemStack removeUpgrade(IUpgradeItem upgrade) {
		var list = new ArrayList<>(upgrades);
		if (!list.remove(upgrade)) return ItemStack.EMPTY;
		if (holderItem.getRemaining(materials, list) < 0) return ItemStack.EMPTY;
		var ans = golem.copy();
		GolemHolder.setUpgrades(ans, list);
		return ans;
	}

	@Override
	public void setStackInSlot(int slot, @NotNull ItemStack stack) {
		if (golem.isEmpty()) return;
		var old = getStackInSlot(slot);
		if (client || ItemStack.matches(old, stack)) return;
		boolean match = ItemStack.isSameItemSameTags(old, stack);
		int remove = match ? old.getCount() - stack.getCount() : old.getCount();
		int add = match ? stack.getCount() - old.getCount() : stack.getCount();
		if (remove > 0) {
			for (int i = 0; i < remove; i++) {
				extractItem(slot, 1, false);
			}
		}
		if (add > 0) {
			for (int i = 0; i < add; i++) {
				insertItem(slot, stack.copyWithCount(1), false);
			}
		}
	}

	@Override
	public int getSlots() {
		return SIZE;
	}

	@Override
	public @NotNull ItemStack getStackInSlot(int slot) {
		int index = slot + SIZE * data[0];
		if (golem.isEmpty() || index < 0 || index >= upgradeList.size()) return ItemStack.EMPTY;
		return upgradeList.get(index);
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) return stack;
		if (!(stack.getItem() instanceof UpgradeItem item)) return stack;
		var ans = appendUpgrade(item);
		if (ans.isEmpty()) return stack;
		if (!simulate) {
			parent.get().set(ans);
		}
		return stack.getCount() == 1 ? ItemStack.EMPTY : stack.copyWithCount(stack.getCount() - 1);
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		var stack = getStackInSlot(slot);
		if (stack.isEmpty() || !(stack.getItem() instanceof UpgradeItem item)) return ItemStack.EMPTY;
		var ans = removeUpgrade(item);
		if (ans.isEmpty()) return ItemStack.EMPTY;
		if (!simulate) {
			parent.get().set(ans);
		}
		return stack.copyWithCount(1);
	}

	@Override
	public int getSlotLimit(int slot) {
		return 1;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		if (golem.isEmpty()) return false;
		var old = getStackInSlot(slot);
		if (!old.isEmpty())
			return false;
		if (!(stack.getItem() instanceof UpgradeItem))
			return false;
		if (stack.getItem() == lastForbidTest)
			return false;
		if (insertItem(slot, stack.copyWithCount(1), true).isEmpty()) {
			return true;
		}
		lastForbidTest = stack.getItem();
		return false;
	}

}
