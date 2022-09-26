package dev.xkmc.modulargolems.compat.materials.create;

import dev.xkmc.l2library.repack.registrate.providers.RegistrateLangProvider;
import dev.xkmc.l2library.repack.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.network.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.ModDispatch;
import dev.xkmc.modulargolems.compat.materials.twilightforest.TFCompatRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import twilightforest.init.TFBlocks;
import twilightforest.init.TFItems;

import static dev.xkmc.modulargolems.init.data.RecipeGen.unlock;

public class CreateDispatch extends ModDispatch {

	public static final String MODID = "create";

	public CreateDispatch() {
	}

	public void genLang(RegistrateLangProvider pvd) {
		pvd.add("golem_material." + MODID + ".zinc", "Zinc");
		pvd.add("golem_material." + MODID + ".andesite_alloy", "Andesite Alloy");
		pvd.add("golem_material." + MODID + ".brass", "Brass");
		pvd.add("golem_material." + MODID + ".railway", "Railway");
	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {
	}

	@Override
	protected ConfigDataProvider getDataGen(DataGenerator gen) {
		return new CreateConfigGen(gen);
	}

}
