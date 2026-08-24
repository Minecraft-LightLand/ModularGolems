package dev.xkmc.modulargolems.content.entity.humanoid;

import dev.xkmc.modulargolems.content.entity.common.SweepGolemMenuControl;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsMenu;
import dev.xkmc.modulargolems.events.event.GolemEquipItemEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Optional;
import java.util.Set;

public class HumanoidGolemMenuControl extends SweepGolemMenuControl<HumanoidGolemEntity> {

	public HumanoidGolemMenuControl(EquipmentsMenu menu, HumanoidGolemEntity golem) {
		super(menu, golem);
	}

	public boolean isValid(EquipmentSlot slot, ItemStack stack) {
		if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND)
			return true;
		return super.isValid(slot, stack);
	}

	public Set<EquipmentSlot> getSlotForItem(ItemStack stack) {
		if (!menu.stillValid(menu.inventory.player) || golem == null) {
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

	public Optional<HumanoidGolemScreenControl> getScreenProvider() {
		return Optional.of(new HumanoidGolemScreenControl(this));
	}

}
