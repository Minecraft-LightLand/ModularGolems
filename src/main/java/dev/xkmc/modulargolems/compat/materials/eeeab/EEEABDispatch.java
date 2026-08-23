package dev.xkmc.modulargolems.compat.materials.eeeab;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.init.loot.MGGLMGen;
import net.minecraft.data.DataGenerator;
import org.jetbrains.annotations.Nullable;

public class EEEABDispatch extends ModDispatch {

	public static final String MODID = EEEABMobs.MOD_ID;

	public EEEABDispatch() {
		super(() -> EEEABClient::new);
		EEEABCompatRegistry.register();
	}

	@Override
	protected void genLang(RegistrateLangProvider pvd) {
		pvd.add("golem_material." + MODID + ".realm", "Realm");
	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {
	}

	@Override
	public @Nullable ConfigDataProvider getDataGen(DataGenerator gen) {
		return new EEEABConfigGen(gen);
	}

	@Override
	public void genLootModifier(MGGLMGen pvd) {
	}

}
