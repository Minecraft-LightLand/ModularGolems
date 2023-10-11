package dev.xkmc.modulargolems.compat.curio;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkHooks;
import top.theillusivec4.curios.api.event.SlotModifiersUpdatedEvent;

public class CuriosEventHandler {

	@SubscribeEvent
	public static void onSlotModifierUpdate(SlotModifiersUpdatedEvent event) {
		if (event.getEntity() instanceof AbstractGolemEntity<?, ?> golem) {
			if (golem.level() instanceof ServerLevel sl) {
				for (var player : sl.players()) {
					if (player.containerMenu instanceof GolemCuriosListMenu menu) {
						if (menu.curios.golem == golem) {
							ItemStack stack = menu.getCarried();
							menu.setCarried(ItemStack.EMPTY);
							var pvd = new GolemCuriosMenuPvd(golem, 0);
							NetworkHooks.openScreen(player, pvd, pvd::writeBuffer);
							player.containerMenu.setCarried(stack);
						}
					}
				}
			} else {
				CurioCompatRegistry.freezeMenu(golem);
			}
		}
	}

}
