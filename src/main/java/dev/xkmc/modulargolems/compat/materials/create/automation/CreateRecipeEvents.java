package dev.xkmc.modulargolems.compat.materials.create.automation;

import com.simibubi.create.content.kinetics.deployer.DeployerRecipeSearchEvent;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.card.ConfigCard;
import dev.xkmc.modulargolems.content.item.equipments.GolemEquipmentItem;
import dev.xkmc.modulargolems.content.item.golem.GolemEquipUtil;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.upgrade.UpgradeItem;
import dev.xkmc.modulargolems.events.CraftEventListeners;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;

public class CreateRecipeEvents {

	private static final ResourceLocation RL = ModularGolems.loc("deployer_recipe");

	@SubscribeEvent
	public static void addRecipe(DeployerRecipeSearchEvent event) {
		ItemStack first = event.getInventory().getItem(0);
		ItemStack second = event.getInventory().getItem(1);
		if (!(first.getItem() instanceof GolemHolder<?, ?> holder)) return;
		ItemStack result;
		Level level = event.getBlockEntity().getLevel();
		if (!(level instanceof ServerLevel sl)) return;
		var ctx = new GolemEquipUtil(false, level);
		result = ctx.applyItemOnHolder(holder, first.copy(), second.copy());
		if (result.isEmpty()) return;
		event.addRecipe(() -> Optional.of(new RecipeHolder<>(RL, new DeployerUpgradeRecipe(result))), 1000);
	}

}
