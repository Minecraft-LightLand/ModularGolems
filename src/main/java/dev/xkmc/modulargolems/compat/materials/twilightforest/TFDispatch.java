package dev.xkmc.modulargolems.compat.materials.twilightforest;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import net.minecraft.data.DataGenerator;

public class TFDispatch extends ModDispatch {

	public static final String MODID = "twilightforest";

	public TFDispatch() {
		super(() -> TFClient::new);
		TFCompatRegistry.register();
	}

	public void genLang(RegistrateLangProvider pvd) {
		pvd.add("golem_material." + MODID + ".ironwood", "Ironwood");
		pvd.add("golem_material." + MODID + ".steeleaf", "Steeleaf");
		pvd.add("golem_material." + MODID + ".knightmetal", "Knightmetal");
		pvd.add("golem_material." + MODID + ".fiery", "Fiery");
	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {
		TFRecipeGen.genRecipe(pvd);
	}

	@Override
	public ConfigDataProvider getDataGen(DataGenerator gen) {
		return new TFConfigGen(gen);
	}

}
