package dev.xkmc.modulargolems.compat.materials.allthemodium;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2core.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ATMDispatch extends ModDispatch {

	public static final String MODID = "allthemodium";

	public ATMDispatch() {
	}

	@Override
	protected void genLang(RegistrateLangProvider pvd) {
		pvd.add("golem_material." + MODID + ".allthemodium", "Allthemodium");
		pvd.add("golem_material." + MODID + ".vibranium", "Vibranium");
		pvd.add("golem_material." + MODID + ".unobtainium", "Unobtainium");
	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {
	}

	@Override
	public @Nullable ConfigDataProvider getDataGen(DataGenerator gen, CompletableFuture<HolderLookup.Provider> pvd) {
		return new ATMConfigGen(gen, pvd);
	}

}
