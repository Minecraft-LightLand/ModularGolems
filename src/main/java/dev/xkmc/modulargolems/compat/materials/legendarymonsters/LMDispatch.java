package dev.xkmc.modulargolems.compat.materials.legendarymonsters;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import net.miauczel.legendary_monsters.LegendaryMonsters;
import net.minecraft.data.DataGenerator;
import org.jetbrains.annotations.Nullable;

public class LMDispatch extends ModDispatch {

	public static final String MODID = LegendaryMonsters.MOD_ID;

	public LMDispatch() {
		LMCompatRegistry.register();
	}

	@Override
	protected void genLang(RegistrateLangProvider pvd) {
		pvd.add("golem_material." + MODID + ".molten_metal", "Molten Metal");
	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {

	}

	@Override
	public @Nullable ConfigDataProvider getDataGen(DataGenerator gen) {
		return new LMConfigGen(gen);
	}

}
