package dev.xkmc.modulargolems.content.core;

import dev.xkmc.mob_weapon_api.api.ai.ItemWrapper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsMenu;
import dev.xkmc.modulargolems.events.event.GolemEquipItemEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class GolemMenuControl<T extends AbstractGolemEntity<T, ?>> {

	public final EquipmentsMenu menu;
	public final T golem;

	public GolemMenuControl(EquipmentsMenu menu, T golem) {
		this.menu = menu;
		this.golem = golem;
	}

	public abstract void fillMenu();

	protected void addSlot(String name, Supplier<ItemWrapper> wrapper, boolean quickMovable,
			Predicate<ItemStack> pred) {
		menu.bindSlot(name, wrapper, quickMovable);
		menu.addSlot(name, pred);
	}

	protected void addSlot(String name, Supplier<ItemWrapper> wrapper, boolean quickMovable,
			BiPredicate<Integer, ItemStack> pred) {
		menu.bindSlot(name, wrapper, quickMovable);
		menu.addSlot(name, pred);
	}

	/**
	 * Same as the predicate overload but lets the slot cap its own stack size, used by the cosmetic
	 * slots (banner, skin) that must only ever hold a single item.
	 */
	protected void addSlot(String name, Supplier<ItemWrapper> wrapper, boolean quickMovable,
			Predicate<ItemStack> pred, int max) {
		menu.bindSlot(name, wrapper, quickMovable);
		menu.addSlot(name, pred, slot -> slot.setMax(max));
	}

	/**
	 * Marks an already registered slot as low priority for shift click, so it is only filled after the
	 * specialised slots have declined the item.
	 */
	protected void lowPriority(String name) {
		menu.markLowPriority(name);
	}

	public boolean isValid(EquipmentSlot slot, ItemStack stack) {
		var valids = getSlotForItem(stack);
		return valids.contains(slot);
	}

	public Set<EquipmentSlot> getSlotForItem(ItemStack stack) {
		if (!menu.stillValid(menu.inventory.player)) {
			return Set.of();
		}
		if (!stack.getItem().canFitInsideContainerItems()) return Set.of();
		if (stack.getItem() instanceof GolemHolder) return Set.of();
		GolemEquipItemEvent event = new GolemEquipItemEvent(golem, stack);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.canEquip()) {
			return Set.of(event.getSlot());
		}
		return Set.of();
	}

	/**
	 * Targets for shift clicking that the generic pass cannot resolve. The arrow slots accept anything
	 * so that unusual arrow items can be placed by hand, which also makes them unusable for resolving
	 * shift clicks, so arrows are routed here instead.
	 */
	public void handleQuickMove(ItemStack stack) {
		if (stack.getItem() instanceof ArrowItem) {
			for (String name : new String[] {"arrow_1", "arrow_2"}) {
				int slot = menu.getSlotIndex(name);
				if (slot < 0) continue;
				if (menu.moveItemStackTo(stack, 36 + slot, 37 + slot, false)) return;
			}
		}
	}

	public abstract Optional<? extends GolemScreenControl<T>> getScreenProvider();

}
