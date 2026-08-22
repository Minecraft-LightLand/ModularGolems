package dev.xkmc.modulargolems.compat.materials.legendarymonsters;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.init.loot.MGGLMGen;
import net.miauczel.legendary_monsters.LegendaryMonsters;
import net.minecraft.data.DataGenerator;
import org.jetbrains.annotations.Nullable;

public class LMDispatch extends ModDispatch {

	public static final String MODID = LegendaryMonsters.MOD_ID;

	public LMDispatch() {
		super(() -> LMClient::new);
		LMCompatRegistry.register();
	}

	@Override
	protected void genLang(RegistrateLangProvider pvd) {
		pvd.add("golem_material." + MODID + ".molten_metal", "Molten Metal");
		pvd.add("golem_material." + MODID + ".cloud", "Cloud");
		pvd.add("golem_material." + MODID + ".obliterator", "Obliterator");
		pvd.add("golem_material." + MODID + ".paladin", "Paladin");
	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {
		LMProxy.genRecipe(pvd);
	}

	@Override
	public @Nullable ConfigDataProvider getDataGen(DataGenerator gen) {
		return new LMConfigGen(gen);
	}

	@Override
	public void genLootModifier(MGGLMGen pvd) {
		LMProxy.genLootModifier(pvd);
	}

}
