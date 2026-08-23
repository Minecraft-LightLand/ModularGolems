package dev.xkmc.modulargolems.compat.materials.eeeab;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.init.loot.MGGLMGen;
import net.miauczel.legendary_monsters.LegendaryMonsters;
import net.minecraft.data.DataGenerator;
import org.jetbrains.annotations.Nullable;

public class E3ABDispatch extends ModDispatch {

	public static final String MODID = EEEABMobs.MOD_ID;

	public E3ABDispatch() {
		super(() -> E3ABClient::new);
		E3ABCompatRegistry.register();
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
		return new E3ABConfigGen(gen);
	}

	@Override
	public void genLootModifier(MGGLMGen pvd) {
	}

}
