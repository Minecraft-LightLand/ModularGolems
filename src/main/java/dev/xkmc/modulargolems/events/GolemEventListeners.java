package dev.xkmc.modulargolems.events;

import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.item.equipments.IGolemEquipmentItem;
import dev.xkmc.modulargolems.events.event.GolemEquipItemEvent;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BowItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;


@EventBusSubscriber(modid = ModularGolems.MODID, bus = EventBusSubscriber.Bus.GAME)
public class GolemEventListeners {

	@SubscribeEvent
	public static void onEquip(GolemEquipItemEvent event) {
		var golem = event.getEntity();
		var stack = event.getStack();
		if (golem instanceof HumanoidGolemEntity) {
			if (stack.getItem() instanceof ArrowItem) {
				event.setSlot(stack.getCount(), EquipmentSlot.OFFHAND);
			}
			if (stack.getItem() instanceof BowItem) {
				event.setSlot(1, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
			}
			if (stack.getItem() instanceof BannerItem) {
				event.setSlot(1, EquipmentSlot.HEAD);
			}

			var slot = golem.getEquipmentSlotForItem(stack);
			if (stack.canEquip(slot, golem)) {
				if (slot == EquipmentSlot.MAINHAND) {
					event.setSlot(1, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
				} else event.setSlot(1, slot);
			}
		}

		if (golem instanceof MetalGolemEntity) {
			if (stack.getItem() instanceof IGolemEquipmentItem item && item.isFor(golem.getType())) {
				event.setSlot(1, item.getSlot());
			} else if (stack.is(MGTagGen.LARGE_GOLEM_WEAPONS)) {
				event.setSlot(1, EquipmentSlot.MAINHAND);
			} else if (stack.getItem() instanceof BannerItem) {
				event.setSlot(1, EquipmentSlot.HEAD, EquipmentSlot.FEET);
			} else if (stack.getItem() instanceof ArrowItem) {
				event.setSlot(stack.getCount(), EquipmentSlot.OFFHAND);
			}
		}
		if (golem instanceof DogGolemEntity) {
			if (stack.getItem() instanceof BannerItem) {
				event.setSlot(1, EquipmentSlot.HEAD);
			}
			if (stack.is(MGTagGen.C_WOLF_ARMORS))
				event.setSlot(1, EquipmentSlot.BODY);
		}
	}

}
