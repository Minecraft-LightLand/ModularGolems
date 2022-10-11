package dev.xkmc.modulargolems.compat.materials.create;

import dev.xkmc.l2library.repack.registrate.providers.RegistrateLangProvider;
import dev.xkmc.l2library.repack.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.network.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import net.minecraft.data.DataGenerator;

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
