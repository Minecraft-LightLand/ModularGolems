package dev.xkmc.modulargolems.compat.materials.mowziesmobs;

import com.bobmowzie.mowziesmobs.MowziesMobs;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.l2library.serial.recipe.ConditionalRecipeWrapper;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.init.data.RecipeGen;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;

public class MowzieDispatch extends ModDispatch {

	public static final String MODID = MowziesMobs.MODID;

	public MowzieDispatch() {
		MowzieCompatRegistry.register();
	}

	@Override
	protected void genLang(RegistrateLangProvider pvd) {
		pvd.add("golem_material." + MODID + ".wroughtnaut", "Wroughtnaut");
	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {
		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,
								MowzieCompatRegistry.WROUGHTNAUT_INGOT, 9)::unlockedBy,
						Items.IRON_INGOT)
				.pattern("ABA").pattern("BCB").pattern("ABA")
				.define('A', GolemItems.GOLEM_TEMPLATE)
				.define('B', Items.IRON_INGOT)
				.define('C', MowzieCompatRegistry.WROUGHTNAUT_ITEMS)
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		RecipeGen.unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT,
								MowzieCompatRegistry.UPGRADE_SLAM.get())::unlockedBy,
						MowzieCompatRegistry.WROUGHTNAUT_INGOT.get())
				.requires(GolemItems.EMPTY_UPGRADE)
				.requires(MowzieCompatRegistry.WROUGHTNAUT_ITEMS)
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));
	}

	@Override
	public ConfigDataProvider getDataGen(DataGenerator gen) {
		return new MowziesConfigGen(gen);
	}

}
