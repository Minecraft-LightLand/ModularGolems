package dev.xkmc.modulargolems.compat.materials.cataclysm;

import com.github.L_Ender.cataclysm.init.ModItems;
import dev.xkmc.modulargolems.events.event.GolemThrowableEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class CataGolemEventHandler {

	@SubscribeEvent
	public static void onTrident(GolemThrowableEvent event) {
		if (event.getStack().is(ModItems.CORAL_SPEAR.get())) {
			event.setThrowable(level -> CataclysmProxy.coralSpear(event.getEntity(), level, event.getStack()));
		}
	}

}
