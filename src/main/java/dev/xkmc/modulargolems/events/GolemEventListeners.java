package dev.xkmc.modulargolems.events;

import dev.xkmc.modulargolems.events.event.GolemEquipEvent;
import dev.xkmc.modulargolems.events.event.GolemThrowableEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.TridentItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GolemEventListeners {

	@SubscribeEvent
	public static void onEquip(GolemEquipEvent event) {
		if (event.getStack().getItem() instanceof ArrowItem) {
			event.setSlot(EquipmentSlot.OFFHAND, event.getStack().getCount());
		}
	}

	@SubscribeEvent
	public static void isThrowable(GolemThrowableEvent event) {
		if (event.getStack().getItem() instanceof TridentItem) {
			event.setThrowable(level -> {
				var ans = new ThrownTrident(level, event.getEntity(), event.getStack());
				ans.pickup = AbstractArrow.Pickup.DISALLOWED;
				return ans;
			});
		}
	}

}
