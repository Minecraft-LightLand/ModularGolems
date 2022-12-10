package dev.xkmc.modulargolems.compat.materials.l2complements;

import dev.xkmc.l2library.repack.registrate.providers.RegistrateLangProvider;
import dev.xkmc.l2library.repack.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.network.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.common.ConditionalRecipeWrapper;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.init.registrate.GolemItemRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import twilightforest.init.TFBlocks;
import twilightforest.init.TFItems;

public class LCDispatch extends ModDispatch {

	public static final String MODID = "l2complements";

	public LCDispatch() {
		LCCompatRegistry.register();
	}

	public void genLang(RegistrateLangProvider pvd) {
		pvd.add("golem_material." + MODID + ".totemic_gold", "Totemic Gold");
		pvd.add("golem_material." + MODID + ".poseidite", "Poseidite");
		pvd.add("golem_material." + MODID + ".shulkerate", "Shulkerate");
		pvd.add("golem_material." + MODID + ".eternium", "Eternium");
	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {
	}

	@Override
	protected ConfigDataProvider getDataGen(DataGenerator gen) {
		return new LCConfigGen(gen);
	}

}
