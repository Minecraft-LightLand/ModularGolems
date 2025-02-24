package dev.xkmc.modulargolems.compat.materials.tinker;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.compat.materials.tinker.automation.TinkerRecipeGen;
import net.minecraft.data.DataGenerator;
import org.jetbrains.annotations.Nullable;

public class TCDispatch extends ModDispatch {

	@Override
	protected void genLang(RegistrateLangProvider pvd) {

	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {
		TinkerRecipeGen.genRecipe(pvd);
	}

	@Nullable
	@Override
	public ConfigDataProvider getDataGen(DataGenerator gen) {
		return new TinkerConfigGen(gen);
	}

}
