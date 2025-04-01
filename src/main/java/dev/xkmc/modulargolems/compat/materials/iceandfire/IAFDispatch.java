package dev.xkmc.modulargolems.compat.materials.iceandfire;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import net.minecraft.data.DataGenerator;
import org.jetbrains.annotations.Nullable;

public class IAFDispatch extends ModDispatch {

	public static final String MODID = "iceandfire";

	public IAFDispatch() {
		IAFCompatRegistry.register();
	}

	@Override
	protected void genLang(RegistrateLangProvider pvd) {
		pvd.add("golem_material." + MODID + ".fire_dragonsteel", "Fire Dragonsteel");
		pvd.add("golem_material." + MODID + ".ice_dragonsteel", "Ice Dragonsteel");
		pvd.add("golem_material." + MODID + ".lightning_dragonsteel", "Lightning Dragonsteel");
	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {

	}

	@Nullable
	@Override
	public ConfigDataProvider getDataGen(DataGenerator gen) {
		return new IAFConfigGen(gen);
	}

}
