package dev.xkmc.modulargolems.content.menu.equipment;

import dev.xkmc.l2core.base.menu.base.BaseContainerMenu;
import dev.xkmc.l2core.base.menu.base.PredSlot;
import dev.xkmc.l2core.base.menu.base.SpriteManager;
import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.mob_weapon_api.api.ai.ItemWrapper;
import dev.xkmc.modulargolems.content.core.GolemMenuControl;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class EquipmentsMenu extends BaseContainerMenu<EquipmentsMenu> {

	public static EquipmentsMenu fromNetwork(MenuType<EquipmentsMenu> type, int wid, Inventory plInv, RegistryFriendlyByteBuf buf) {
		Entity entity = plInv.player.level().getEntity(buf.readInt());
		return new EquipmentsMenu(type, wid, plInv, entity instanceof AbstractGolemEntity<?, ?> golem ? golem : null);
	}

	public static EquipmentSlot[] SLOTS = {EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
	public static EquipmentSlot[] DOG_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.BODY};

	public static final SpriteManager MANAGER = new SpriteManager(ModularGolems.MODID, "equipments");

	private static final int PLAYER_SLOTS = 36;

	@Nullable
	public final AbstractGolemEntity<?, ?> golem;
	@Nullable
	public final GolemMenuControl<?> ctrl;
	protected final EquipmentSlot[] equipmentSlots;

	private final List<String> slotNames = new ArrayList<>();
	private final Map<String, Supplier<ItemWrapper>> slotWrappers = new HashMap<>();
	private final Set<String> noQuickMoveSlots = new HashSet<>();
	private final Set<String> lowPrioritySlots = new HashSet<>();

	protected EquipmentsMenu(MenuType<?> type, int wid, Inventory plInv, @Nullable AbstractGolemEntity<?, ?> golem) {
		super(type, wid, plInv, MANAGER, EquipmentsContainer::new, false);
		this.golem = golem;
		if (golem == null) {
			equipmentSlots = SLOTS;
			ctrl = null;
			return;
		}
		ctrl = GolemType.getGolemType(golem.getType()).menuControl(this, Wrappers.cast(golem));
		equipmentSlots = ctrl.getSlotDefinition();
		ctrl.fillMenu();
	}

	@Override
	public boolean stillValid(Player player) {
		if (golem == null || !player.isAlive()) return false;
		golem.inventoryTick = 5;
		return !golem.isRemoved() && golem.getGuardedDataImpl() > 0;
	}

	@Override
	public PredSlot getAsPredSlot(String name, int i, int j) {
		return super.getAsPredSlot(name, i, j);
	}

	@Override
	public ItemStack quickMoveStack(Player pl, int id) {
		if (golem != null && ctrl != null) {
			ItemStack stack = this.slots.get(id).getItem();
			boolean placed;
			if (id >= PLAYER_SLOTS) {
				placed = this.moveItemStackTo(stack, 0, PLAYER_SLOTS, true);
			} else {
				ctrl.handleQuickMove(stack);
				placed = false;
				for (int pass = 0; pass < 2 && !placed; pass++) {
					boolean wantLow = pass == 1;
					for (int i = 0; i < slotNames.size(); i++) {
						String name = slotNames.get(i);
						if (noQuickMoveSlots.contains(name)) continue;
						if (lowPrioritySlots.contains(name) != wantLow) continue;
						int dest = PLAYER_SLOTS + i;
						if (dest == id) continue;
						if (this.moveItemStackTo(stack, dest, dest + 1, false)) {
							placed = true;
							break;
						}
					}
				}
			}
			if (placed) {
				this.slots.get(id).setChanged();
				this.container.setChanged();
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void addSlot(String name, Predicate<ItemStack> pred) {
		slotNames.add(name);
		super.addSlot(name, pred);
	}

	@Override
	public void addSlot(String name, BiPredicate<Integer, ItemStack> pred) {
		slotNames.add(name);
		super.addSlot(name, pred);
	}

	@Override
	public void addSlot(String name, Predicate<ItemStack> pred, Consumer<PredSlot> consumer) {
		slotNames.add(name);
		super.addSlot(name, pred, consumer);
	}

	public void bindSlot(String name, Supplier<ItemWrapper> wrapper, boolean quickMovable) {
		slotWrappers.put(name, wrapper);
		if (!quickMovable) noQuickMoveSlots.add(name);
	}

	public void markLowPriority(String name) {
		lowPrioritySlots.add(name);
	}

	public String getSlotNameAt(int index) {
		return index >= 0 && index < slotNames.size() ? slotNames.get(index) : "";
	}

	public String getSlotNameAtMenuSlot(int containerSlot) {
		return getSlotNameAt(containerSlot - PLAYER_SLOTS);
	}

	public int getSlotIndex(String name) {
		return slotNames.indexOf(name);
	}

	public ItemWrapper getWrapperAt(int index) {
		var supplier = slotWrappers.get(getSlotNameAt(index));
		return supplier == null ? ItemWrapper.EMPTY : supplier.get();
	}

	public boolean isSlotEmpty(String name) {
		int index = getSlotIndex(name);
		if (index < 0) return true;
		int position = (slots.size() - slotNames.size()) + index;
		return position >= slots.size() || slots.get(position).getItem().isEmpty();
	}

	@Override
	public boolean moveItemStackTo(ItemStack p_38904_, int p_38905_, int p_38906_, boolean p_38907_) {
		return super.moveItemStackTo(p_38904_, p_38905_, p_38906_, p_38907_);
	}

}
