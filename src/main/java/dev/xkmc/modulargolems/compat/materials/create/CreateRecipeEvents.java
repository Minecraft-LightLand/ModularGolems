package dev.xkmc.modulargolems.compat.materials.create;

import com.simibubi.create.content.kinetics.deployer.DeployerRecipeSearchEvent;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.upgrade.UpgradeItem;
import dev.xkmc.modulargolems.events.CraftEventListeners;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Optional;

public class CreateRecipeEvents {

	@SubscribeEvent
	public static void addRecipe(DeployerRecipeSearchEvent event) {
		ItemStack first = event.getInventory().getItem(0);
		ItemStack second = event.getInventory().getItem(1);
		if (first.getItem() instanceof GolemHolder<?, ?> holder &&
				second.getItem() instanceof UpgradeItem upgrade) {
			ItemStack result = CraftEventListeners.appendUpgrade(first, holder, upgrade);
			if (!result.isEmpty()) {
				event.addRecipe(() -> Optional.of(new DeployerUpgradeRecipe(result)), 1000);
			}
		}
	}

}
