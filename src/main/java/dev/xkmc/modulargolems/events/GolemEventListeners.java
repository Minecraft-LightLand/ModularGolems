package dev.xkmc.modulargolems.events;

import dev.xkmc.modulargolems.events.event.GolemEquipEvent;
import dev.xkmc.modulargolems.events.event.GolemThrowableEvent;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.TridentItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = ModularGolems.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GolemEventListeners {

	@SubscribeEvent
	public static void onEquip(GolemEquipEvent event) {
		if (event.getStack().getItem() instanceof ArrowItem) {
			event.setSlot(EquipmentSlot.OFFHAND, event.getStack().getCount());
		}
		if (!event.getEntity().getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
			if (event.getStack().getItem() instanceof BowItem) {
				event.setSlot(EquipmentSlot.OFFHAND, 1);
			}
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
