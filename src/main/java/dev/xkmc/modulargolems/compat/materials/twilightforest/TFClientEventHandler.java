package dev.xkmc.modulargolems.compat.materials.twilightforest;

import dev.xkmc.modulargolems.events.event.GolemRenderItemInHandEvent;
import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.SubscribeEvent;
import twilightforest.block.GiantBlock;
import twilightforest.item.GiantPickItem;
import twilightforest.item.GiantSwordItem;

public class TFClientEventHandler {

	@SubscribeEvent
	public static void onGolemItemRender(GolemRenderItemInHandEvent event) {//TODO
		if (event.stack.getItem() instanceof GiantPickItem ||event.stack.getItem() instanceof GiantSwordItem ||
				event.stack.getItem() instanceof BlockItem block && block.getBlock() instanceof GiantBlock) {
			float r = 1f / event.entity.getScale();
			event.pose.scale(r, r, r);
		}
	}

}
