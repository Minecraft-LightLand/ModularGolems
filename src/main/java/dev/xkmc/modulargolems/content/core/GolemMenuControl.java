package dev.xkmc.modulargolems.content.core;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsMenu;
import dev.xkmc.modulargolems.events.event.GolemEquipItemEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Optional;
import java.util.Set;

public abstract class GolemMenuControl<T extends AbstractGolemEntity<T, ?>> {

	public final EquipmentsMenu menu;
	public final T golem;

	public GolemMenuControl(EquipmentsMenu menu, T golem) {
		this.menu = menu;
		this.golem = golem;
	}

	public abstract void fillMenu();

	public boolean isValid(EquipmentSlot slot, ItemStack stack) {
		var valids = getSlotForItem(stack);
		return valids.contains(slot);
	}

	public Set<EquipmentSlot> getSlotForItem(ItemStack stack) {
		if (!menu.stillValid(menu.inventory.player)) {
			return Set.of();
		}
		if (!stack.canFitInsideContainerItems()) return Set.of();
		if (stack.getItem() instanceof GolemHolder) return Set.of();
		GolemEquipItemEvent event = new GolemEquipItemEvent(golem, stack);
		NeoForge.EVENT_BUS.post(event);
		if (event.canEquip()) {
			return Set.of(event.getSlot());
		}
		return Set.of();
	}

	public void handleQuickMove(ItemStack stack) {
	}

	public abstract EquipmentSlot[] getSlotDefinition();

	public abstract Optional<? extends GolemScreenControl<T>> getScreenProvider();

}
