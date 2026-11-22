package dev.xkmc.modulargolems.content.entity.dog;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class DogRiderRenderBlocker {

	@SubscribeEvent
	public static void onPreRender(RenderLivingEvent.Pre<?, ?> event) {
		var uuid = event.getEntity().getUUID();
		if (!DogRiderLayer.BLOCKED.contains(uuid)) return;
		var mc = Minecraft.getInstance();
		boolean firstPerson = event.getEntity() == mc.getCameraEntity() && mc.options.getCameraType().isFirstPerson();
		if (!firstPerson) event.setCanceled(true);
		DogRiderLayer.BLOCKED.remove(uuid);
	}

}
