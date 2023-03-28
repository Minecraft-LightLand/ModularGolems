package dev.xkmc.modulargolems.events;

import dev.xkmc.modulargolems.content.entity.humanoid.ranged.GolemShooterHelper;
import dev.xkmc.modulargolems.events.event.GolemEquipEvent;
import dev.xkmc.modulargolems.events.event.GolemThrowableEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GolemEventListeners {

	@SubscribeEvent
	public static void onEquip(GolemEquipEvent event) {
		ItemStack stack = event.getStack();
		if (stack.getItem() instanceof ArrowItem) {
			event.setSlot(EquipmentSlot.OFFHAND, event.getStack().getCount());
		}
		if (event.getEntity().getMainHandItem().canPerformAction(ToolActions.SHIELD_BLOCK) && (
				(stack.getItem() instanceof BowItem) ||
						(stack.getItem() instanceof CrossbowItem) ||
						GolemShooterHelper.isValidThrowableWeapon(event.getEntity(), stack,
								InteractionHand.OFF_HAND).isThrowable())) {
			event.setSlot(EquipmentSlot.OFFHAND, 1);
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
