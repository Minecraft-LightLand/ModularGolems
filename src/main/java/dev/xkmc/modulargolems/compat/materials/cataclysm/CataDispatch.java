package dev.xkmc.modulargolems.compat.materials.cataclysm;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.content.client.override.ModelOverride;
import dev.xkmc.modulargolems.content.client.override.ModelOverrides;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CataDispatch extends ModDispatch {

	public static final String MODID = "cataclysm";

	public CataDispatch() {
		CataCompatRegistry.register();
	}

	public void genLang(RegistrateLangProvider pvd) {
		pvd.add("golem_material." + MODID + ".ignitium", "Ignitium");
		pvd.add("golem_material." + MODID + ".witherite", "Witherite");
	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {
	}

	@Override
	public ConfigDataProvider getDataGen(DataGenerator gen) {
		return new CataConfigGen(gen);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void dispatchClientSetup() {
		ModelOverrides.registerOverride(new ResourceLocation(CataDispatch.MODID, "ignitium"),
				ModelOverride.texturePredicate((e) -> e.getHealth() <= e.getMaxHealth() / 2 ? "_soul" : ""));
	}
}
