package dev.xkmc.modulargolems.content.menu.equipment;

import dev.xkmc.l2library.base.menu.base.BaseContainerMenu;
import dev.xkmc.l2library.base.menu.base.PredSlot;
import dev.xkmc.l2library.base.menu.base.SpriteManager;
import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.mob_weapon_api.api.ai.ItemWrapper;
import dev.xkmc.modulargolems.content.core.GolemMenuControl;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class EquipmentsMenu extends BaseContainerMenu<EquipmentsMenu> {

	public static EquipmentsMenu fromNetwork(MenuType<EquipmentsMenu> type, int wid, Inventory plInv, FriendlyByteBuf buf) {
		assert Proxy.getClientWorld() != null;
		Entity entity = Proxy.getClientWorld().getEntity(buf.readInt());
		return new EquipmentsMenu(type, wid, plInv, entity instanceof AbstractGolemEntity<?, ?> golem ? golem : null);
	}

	public static EquipmentSlot[] SLOTS = {EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
	public static EquipmentSlot[] DOG_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST};

	public static final SpriteManager MANAGER = new SpriteManager(ModularGolems.MODID, "equipments");

	private final List<String> slotNames = new ArrayList<>();
	private final Map<String, Supplier<ItemWrapper>> slotWrappers = new HashMap<>();
	private final Set<String> noQuickMoveSlots = new HashSet<>();
	/**
	 * Slots that accept any item and should therefore be offered last on shift click, so that the
	 * specialised slots (armor, banner, skin, patrol path) get first pick of whatever fits them.
	 */
	private final Set<String> lowPrioritySlots = new HashSet<>();

	@Nullable
	public final AbstractGolemEntity<?, ?> golem;
	@Nullable
	public final GolemMenuControl<?> ctrl;

	protected EquipmentsMenu(MenuType<?> type, int wid, Inventory plInv, @Nullable AbstractGolemEntity<?, ?> golem) {
		super(type, wid, plInv, MANAGER, EquipmentsContainer::new, false);
		this.golem = golem;
		if (golem == null) {
			ctrl = null;
			return;
		}
		ctrl = GolemType.getGolemType(golem.getType()).menuControl(this, Wrappers.cast(golem));
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

	/**
	 * The golem slots are laid out after the player inventory, whose slots 0-8 are the hotbar and
	 * 9-35 the main inventory, matching the vanilla player inventory layout.
	 */
	private static final int PLAYER_SLOTS = 36;

	@Override
	public ItemStack quickMoveStack(Player pl, int id) {
		if (golem != null && ctrl != null) {
			ItemStack stack = this.slots.get(id).getItem();
			boolean placed;
			if (id >= PLAYER_SLOTS) {
				placed = this.moveItemStackTo(stack, 0, PLAYER_SLOTS, true);
			} else {
				ctrl.handleQuickMove(stack);
				// Offer the stack to every golem slot, mirroring a layout wide range move, but skip
				// slots accepting anything since they would swallow every item, and skip the slot
				// the move started from or it merges the stack into itself.
				placed = false;
				// Two passes: specialised slots first, then the catch-all slots that accept anything
				// (marked low priority), so shift click never dumps an item into a generic slot while
				// a slot that specifically wants it is still free.
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

	/**
	 * Binds the entity inventory this slot reads and writes. Must be called before
	 * {@link #addSlot(String, Predicate)}. A slot that is not quick movable still accepts items by
	 * hand, it is just skipped when resolving shift clicks, which is what slots accepting anything
	 * need.
	 */
	public void bindSlot(String name, Supplier<ItemWrapper> wrapper, boolean quickMovable) {
		slotWrappers.put(name, wrapper);
		if (!quickMovable) {
			noQuickMoveSlots.add(name);
		}
	}

	/**
	 * Marks a slot as the lowest priority target for shift click: it is only offered items after all
	 * other golem slots have declined them.
	 */
	public void markLowPriority(String name) {
		lowPrioritySlots.add(name);
	}

	/**
	 * Resolves a container index, which counts the golem slots only, to a component name.
	 */
	public String getSlotNameAt(int index) {
		return index >= 0 && index < slotNames.size() ? slotNames.get(index) : "";
	}

	/**
	 * Same as {@link #getSlotNameAt} but for a menu slot index, which counts the player inventory
	 * (36 slots, hotbar first) before the golem slots. Tooltips must use this: hovering a player
	 * slot otherwise maps onto whichever golem slot happens to share its index, so the first row of
	 * the player inventory would report "banner" / "skin" / "route".
	 */
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

	/**
	 * Whether the named slot holds nothing. Used by the screen controls instead of
	 * {@code getAsPredSlot}, which relies on the layout's own slot map.
	 */
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
