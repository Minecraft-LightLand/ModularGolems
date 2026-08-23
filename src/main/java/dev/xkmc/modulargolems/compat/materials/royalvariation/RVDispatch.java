package dev.xkmc.modulargolems.compat.materials.royalvariation;

import com.mongoose.royalvariations.RoyalVariations;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.init.loot.MGGLMGen;
import net.minecraft.data.DataGenerator;
import org.jetbrains.annotations.Nullable;

public class RVDispatch extends ModDispatch {

	public static final String MODID = RoyalVariations.MOD_ID;

	public RVDispatch() {
		super(() -> RVClient::new);
		RVCompatRegistry.register();
	}

	@Override
	protected void genLang(RegistrateLangProvider pvd) {
		pvd.add("golem_material." + MODID + ".royal", "Royal");
	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {
	}

	@Override
	public @Nullable ConfigDataProvider getDataGen(DataGenerator gen) {
		return new RVConfigGen(gen);
	}

	@Override
	public void genLootModifier(MGGLMGen pvd) {
	}

}
