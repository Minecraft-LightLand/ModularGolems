package dev.xkmc.modulargolems.compat.materials.twilightforest;

import dev.xkmc.modulargolems.events.event.GolemRenderItemInHandEvent;
import net.minecraft.world.item.BlockItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import twilightforest.block.GiantBlock;
import twilightforest.item.GiantItem;

public class TFClientEventHandler {

	@SubscribeEvent
	public static void onGolemItemRender(GolemRenderItemInHandEvent event) {
		if (event.stack.getItem() instanceof GiantItem ||
				event.stack.getItem() instanceof BlockItem block && block.getBlock() instanceof GiantBlock) {
			float r = 1f / event.entity.getScale();
			event.pose.scale(r, r, r);
		}
	}

}
