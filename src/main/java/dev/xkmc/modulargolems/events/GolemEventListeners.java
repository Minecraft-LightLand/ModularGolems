package dev.xkmc.modulargolems.events;

import dev.xkmc.modulargolems.events.event.GolemEquipEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArrowItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GolemEventListeners {

	@SubscribeEvent
	public static void onEquip(GolemEquipEvent event) {
		if (event.getStack().getItem() instanceof ArrowItem) {
			event.setSlot(EquipmentSlot.OFFHAND);
		}
	}

}
