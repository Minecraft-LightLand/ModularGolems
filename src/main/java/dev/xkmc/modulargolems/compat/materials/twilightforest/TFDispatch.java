package dev.xkmc.modulargolems.compat.materials.twilightforest;

import dev.xkmc.l2library.repack.registrate.providers.RegistrateLangProvider;
import dev.xkmc.l2library.serial.network.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.ModDispatch;
import net.minecraft.data.DataGenerator;

public class TFDispatch extends ModDispatch {

	public static final String MODID = "twilightforest";

	public TFDispatch() {
		TFModifiers.register();
	}

	public void genLang(RegistrateLangProvider pvd) {
		pvd.add("golem_material." + MODID + ".ironwood", "Ironwood");
		pvd.add("golem_material." + MODID + ".steeleaf", "Steeleaf");
		pvd.add("golem_material." + MODID + ".knightmetal", "Knightmetal");
		pvd.add("golem_material." + MODID + ".fiery", "Fiery");
	}

	@Override
	protected ConfigDataProvider getDataGen(DataGenerator gen) {
		return new TFConfigGen(gen);
	}

}
