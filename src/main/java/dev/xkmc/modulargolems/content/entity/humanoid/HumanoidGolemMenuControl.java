package dev.xkmc.modulargolems.content.entity.humanoid;

import dev.xkmc.modulargolems.content.entity.common.SweepGolemMenuControl;
import dev.xkmc.modulargolems.content.item.card.PathRecordCard;
import dev.xkmc.modulargolems.content.item.golem.GolemFacade;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsMenu;
import dev.xkmc.modulargolems.events.event.GolemEquipEvent;
import dev.xkmc.modulargolems.events.event.GolemEquipItemEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import java.util.Optional;
import java.util.Set;

public class HumanoidGolemMenuControl extends SweepGolemMenuControl<HumanoidGolemEntity> {

	public HumanoidGolemMenuControl(EquipmentsMenu menu, HumanoidGolemEntity golem) {
		super(menu, golem);
	}

	/**
	 * The humanoid has no shoulder weapons, so it reuses the shoulder positions that the larger golem uses for
	 * them, putting the banner and the skin where the larger golem's banner and skin sit beside. The shared
	 * layout keeps both banner and skin positions, which the humanoid simply leaves empty. The patrol path
	 * stays where it is until it gets a position of its own.
	 */
	public void fillMenu() {
		addSlot("right_hand", () -> golem.getWrapperOfHand(EquipmentSlot.MAINHAND), true,
				(i, e) -> isValid(EquipmentSlot.MAINHAND, e));
		addSlot("left_hand", () -> golem.getWrapperOfHand(EquipmentSlot.OFFHAND), true,
				(i, e) -> isValid(EquipmentSlot.OFFHAND, e));
		addSlot("head", () -> golem.getWrapperOfHand(EquipmentSlot.HEAD), true,
				e -> isValid(EquipmentSlot.HEAD, e));
		addSlot("chest", () -> golem.getWrapperOfHand(EquipmentSlot.CHEST), true,
				e -> isValid(EquipmentSlot.CHEST, e));
		addSlot("legs", () -> golem.getWrapperOfHand(EquipmentSlot.LEGS), true,
				e -> isValid(EquipmentSlot.LEGS, e));
		addSlot("feet", () -> golem.getWrapperOfHand(EquipmentSlot.FEET), true,
				e -> isValid(EquipmentSlot.FEET, e));
		addSlot("backup", golem::getBackupHand, true,
				e -> isValid(EquipmentSlot.MAINHAND, e) || isValid(EquipmentSlot.OFFHAND, e));
		addSlot("arrow_1", golem::getArrowSlot, false, e -> true);
		addSlot("arrow_2", golem::getArrowSlot2, false, e -> true);
		addSlot("left_shoulder", golem::getBannerSlot, true, e -> e.getItem() instanceof BannerItem, 1);
		addSlot("right_shoulder", golem::getSkinSlot, true, e -> e.getItem() instanceof GolemFacade, 1);
		addSlot("test", golem::getRouteSlot, true, e -> e.getItem() instanceof PathRecordCard);
		// right_hand / left_hand / backup take any item, so shift click must offer them last, after the
		// armour, banner, skin and patrol slots have had a chance to take what actually fits them.
		lowPriority("right_hand");
		lowPriority("left_hand");
		lowPriority("backup");
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
		if (!stack.getItem().canFitInsideContainerItems()) return Set.of();
		if (stack.getItem() instanceof GolemHolder) return Set.of();

		GolemEquipEvent e1 = new GolemEquipEvent(golem, stack);
		MinecraftForge.EVENT_BUS.post(e1);
		if (e1.canEquip()) {
			return Set.of(e1.getSlot());
		}
		GolemEquipItemEvent event = new GolemEquipItemEvent(golem, stack);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.canEquip()) {
			return Set.of(event.getSlot());
		}
		return Set.of();
	}

	public Optional<HumanoidGolemScreenControl> getScreenProvider() {
		return Optional.of(new HumanoidGolemScreenControl(this));
	}

}
