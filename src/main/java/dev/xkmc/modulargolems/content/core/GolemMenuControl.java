package dev.xkmc.modulargolems.content.core;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsMenu;
import dev.xkmc.modulargolems.events.event.GolemEquipItemEvent;
import net.minecraft.world.entity.EquipmentSlot;
import dev.xkmc.mob_weapon_api.api.ai.ItemWrapper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.Set;

public abstract class GolemMenuControl<T extends AbstractGolemEntity<T, ?>> {

	public final EquipmentsMenu menu;
	public final T golem;

	public GolemMenuControl(EquipmentsMenu menu, T golem) {
		this.menu = menu;
		this.golem = golem;
	}

	public abstract void fillMenu();

	protected void addSlot(String name, Supplier<ItemWrapper> wrapper, boolean quickMovable, Predicate<ItemStack> pred) {
		menu.bindSlot(name, wrapper, quickMovable);
		menu.addSlot(name, pred);
	}

	protected void addSlot(String name, Supplier<ItemWrapper> wrapper, boolean quickMovable, BiPredicate<Integer, ItemStack> pred) {
		menu.bindSlot(name, wrapper, quickMovable);
		menu.addSlot(name, pred);
	}

	protected void addSlot(String name, Supplier<ItemWrapper> wrapper, boolean quickMovable, Predicate<ItemStack> pred, int max) {
		menu.bindSlot(name, wrapper, quickMovable);
		menu.addSlot(name, pred, ps -> ps.setMax(max));
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
		if (stack.getItem() instanceof net.minecraft.world.item.BannerItem) return Set.of();
		GolemEquipItemEvent event = new GolemEquipItemEvent(golem, stack);
		NeoForge.EVENT_BUS.post(event);
		if (event.canEquip()) {
			return Set.of(event.getSlot());
		}
		return Set.of();
	}

	public void handleQuickMove(ItemStack stack) {
		if (stack.getItem() instanceof net.minecraft.world.item.ArrowItem) {
			for (String name : new String[]{"arrow_1", "arrow_2"}) {
				int idx = menu.getSlotIndex(name);
				if (idx < 0) continue;
				int dest = 36 + idx;
				if (menu.moveItemStackTo(stack, dest, dest + 1, false)) return;
			}
		}
	}

	public abstract EquipmentSlot[] getSlotDefinition();

	public abstract Optional<? extends GolemScreenControl<T>> getScreenProvider();

}
