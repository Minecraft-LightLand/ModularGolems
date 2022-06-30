package dev.xkmc.modulargolems.events;

import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.item.GolemPart;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CraftEvents {

	@SubscribeEvent
	public static void onAnvilCraft(AnvilUpdateEvent event) {
		ItemStack stack = event.getLeft();
		ItemStack block = event.getRight();
		if (stack.getItem() instanceof GolemPart) {
			GolemMaterialConfig.getMaterial(block).ifPresent(e -> {
				ItemStack new_stack = stack.copy();
				GolemPart.setMaterial(new_stack, e);
				event.setOutput(new_stack);
				event.setMaterialCost(1);
				event.setCost(0);
			});
		}
	}

}
