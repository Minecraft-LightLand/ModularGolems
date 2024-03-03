package dev.xkmc.modulargolems.compat.materials.cataclysm;

import com.github.L_Ender.cataclysm.init.ModItems;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.l2library.serial.recipe.ConditionalRecipeWrapper;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.content.client.override.ModelOverride;
import dev.xkmc.modulargolems.content.client.override.ModelOverrides;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static dev.xkmc.modulargolems.init.data.RecipeGen.unlock;

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
		unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CataCompatRegistry.ENDER_GUARDIAN.get())::unlockedBy,
				ModItems.GAUNTLET_OF_GUARD.get()).requires(GolemItems.EMPTY_UPGRADE).requires(ModItems.GAUNTLET_OF_GUARD.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CataCompatRegistry.LEVIATHAN.get())::unlockedBy,
				ModItems.TIDAL_CLAWS.get()).requires(GolemItems.EMPTY_UPGRADE).requires(ModItems.TIDAL_CLAWS.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));
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
