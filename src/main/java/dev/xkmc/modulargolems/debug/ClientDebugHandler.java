package dev.xkmc.modulargolems.debug;

import dev.xkmc.modulargolems.events.event.GolemInfoEvent;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ModularGolems.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientDebugHandler {


	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onGolemDebugInfo(GolemInfoEvent event) {
		ClientDebugInfo.append(event);
	}

}
