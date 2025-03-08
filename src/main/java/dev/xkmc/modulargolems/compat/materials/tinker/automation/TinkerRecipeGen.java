package dev.xkmc.modulargolems.compat.materials.tinker.automation;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.recipe.ConditionalRecipeWrapper;
import dev.xkmc.modulargolems.compat.materials.common.CompatManager;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.RecipeGen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.registration.object.FlowingFluidObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;

public class TinkerRecipeGen {

	public static void genRecipe(RegistrateRecipeProvider pvd) {
		findAndTry(pvd, "iron", TinkerFluids.moltenIron, 90);
		findAndTry(pvd, "copper", TinkerFluids.moltenCopper, 90);
		findAndTry(pvd, "gold", TinkerFluids.moltenGold, 90);
		findAndTry(pvd, "netherite", TinkerFluids.moltenNetherite, 90);
		findAndTry(pvd, "zine", TinkerFluids.moltenZinc, 90);
		findAndTry(pvd, "cobalt", TinkerFluids.moltenCobalt, 90);
		findAndTry(pvd, "amethystBronze", TinkerFluids.moltenAmethystBronze, 90);
		findAndTry(pvd, "manyullyn", TinkerFluids.moltenManyullyn, 90);
		findAndTry(pvd, "hepatizon", TinkerFluids.moltenHepatizon, 90);
	}

	private static void findAndTry(RegistrateRecipeProvider pvd, String id, FlowingFluidObject<ForgeFlowingFluid> fluid, int amount) {
		var tag = fluid.getCommonTag();
		if (tag == null) return;
		for (var e : CompatManager.gatherConfig().entrySet()) {
			if (e.getKey().getPath().equals(id)) {
				genCasting(pvd, e.getKey(), fluid, amount);
			}
		}
	}

	private static void genCasting(RegistrateRecipeProvider pvd, ResourceLocation id, FlowingFluidObject<ForgeFlowingFluid> fluid, int ingot) {
		for (var part : GolemPart.LIST) {
			var part_rl = ForgeRegistries.ITEMS.getKey(part);
			assert part_rl != null;
			String item_name = part_rl.getPath();
			var rl = new ResourceLocation(ModularGolems.MODID, id.getPath() + "_casting_" + item_name);

			ItemStack result = GolemPart.setMaterial(part.getDefaultInstance(), id);
			RecipeGen.unlock(pvd, ItemCastingRecipeBuilder.basinRecipe(ItemOutput.fromStack(result))::unlockedBy, part)
					.setCast(part, true).setFluidAndTime(fluid, ingot * part.count)
					.setCoolingTime(1000)
					.save(ConditionalRecipeWrapper.mod(pvd, TConstruct.MOD_ID, id.getNamespace()), rl);
		}
	}

}
