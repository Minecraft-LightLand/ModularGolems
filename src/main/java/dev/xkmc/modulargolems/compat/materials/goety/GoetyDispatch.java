package dev.xkmc.modulargolems.compat.materials.goety;

import com.Polarice3.Goety.Goety;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import net.minecraft.data.DataGenerator;
import org.jetbrains.annotations.Nullable;

public class GoetyDispatch extends ModDispatch {

	public static final String MODID = Goety.MOD_ID;

	public GoetyDispatch() {
		GoetyCompatRegistry.register();
	}

	@Override
	protected void genLang(RegistrateLangProvider pvd) {
		pvd.add("golem_material." + MODID + ".cursed_metal", "Cursed Metal");
		pvd.add("golem_material." + MODID + ".dark_metal", "Dark Metal");
	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {
		GoetyRecipeGen.genRecipe(pvd);
	}

	@Override
	public @Nullable ConfigDataProvider getDataGen(DataGenerator gen) {
		return new GoetyConfigGen(gen);
	}

}
