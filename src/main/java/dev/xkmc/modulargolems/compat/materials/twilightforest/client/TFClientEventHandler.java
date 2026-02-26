package dev.xkmc.modulargolems.compat.materials.twilightforest.client;

import dev.xkmc.modulargolems.compat.materials.twilightforest.TFCompatRegistry;
import dev.xkmc.modulargolems.events.event.GolemRenderItemInHandEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class TFClientEventHandler {

	@SubscribeEvent
	public static void onGolemItemRender(GolemRenderItemInHandEvent event) {
		if (event.stack.is(TFCompatRegistry.GIANT_ITEM)) {
			float r = 1f / event.entity.getScale();
			event.pose.scale(r, r, r);
		}
	}

}
