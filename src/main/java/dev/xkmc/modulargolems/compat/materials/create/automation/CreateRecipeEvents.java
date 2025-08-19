package dev.xkmc.modulargolems.compat.materials.create.automation;

import com.simibubi.create.content.kinetics.deployer.DeployerRecipeSearchEvent;
import dev.xkmc.modulargolems.content.item.golem.GolemEquipUtil;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Optional;

public class CreateRecipeEvents {

	@SubscribeEvent
	public static void addRecipe(DeployerRecipeSearchEvent event) {
		ItemStack first = event.getInventory().getItem(0);
		ItemStack second = event.getInventory().getItem(1);
		if (!(first.getItem() instanceof GolemHolder<?, ?> holder)) return;
		ItemStack result;
		Level level = event.getBlockEntity().getLevel();
		if (!(level instanceof ServerLevel)) return;
		var ctx = new GolemEquipUtil(false, level);
		result = ctx.applyItemOnHolder(holder, first, second);
		if (result.isEmpty()) return;
		event.addRecipe(() -> Optional.of(new DeployerUpgradeRecipe(result)), 1000);
	}


}
