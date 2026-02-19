package dev.xkmc.modulargolems.compat.materials.blazegear;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import net.minecraft.data.DataGenerator;

public class BGDispatch extends ModDispatch {

	public static final String MODID = "blazegear";

	public BGDispatch() {
		super(() -> BGClient::new);
		BGCompatRegistry.register();
	}

	public void genLang(RegistrateLangProvider pvd) {
		pvd.add("golem_material." + MODID + ".brimsteel", "Brimsteel");
	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {
	}

	@Override
	public ConfigDataProvider getDataGen(DataGenerator gen) {
		return new BGConfigGen(gen);
	}

}
