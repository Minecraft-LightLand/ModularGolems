package dev.xkmc.modulargolems.compat.materials.goety.revelation;

import com.Polarice3.Goety.common.items.ModItems;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.l2library.serial.recipe.ConditionalRecipeWrapper;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.compat.materials.goety.GoetyCompatRegistry;
import dev.xkmc.modulargolems.init.data.RecipeGen;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

public class GRDispatch extends ModDispatch {

	public static boolean isLoaded() {
		return ModList.get().isLoaded(GRDispatch.MODID) || !FMLLoader.isProduction();
	}

	public static final String MODID = "goety_revelation";

	public GRDispatch() {
		GRCompatRegistry.register();
	}

	@Override
	protected void genLang(RegistrateLangProvider pvd) {
		pvd.add("golem_material." + MODID + ".apocalyptium", "Apocalyptium");
	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,
						GRCompatRegistry.UPGRADE_CD.get())::unlockedBy, ModItems.UNHOLY_HAT.get())
				.pattern("BFB").pattern("BOB").pattern("BUB")
				.define('U', GolemItems.EMPTY_UPGRADE)
				.define('O', GoetyCompatRegistry.REV_RING)
				.define('F', ModItems.UNHOLY_FABRIC.get())
				.define('B', ModItems.UNHOLY_BLOOD.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,
						GRCompatRegistry.UPGRADE_BOW.get())::unlockedBy, ModItems.UNHOLY_HAT.get())
				.pattern(" B ").pattern("FOF").pattern(" U ")
				.define('U', GolemItems.EMPTY_UPGRADE)
				.define('O', GoetyCompatRegistry.REV_RING)
				.define('F', ModItems.UNHOLY_FABRIC.get())
				.define('B', ModItems.UNHOLY_BLOOD.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,
						GRCompatRegistry.UPGRADE_FAST.get())::unlockedBy, ModItems.UNHOLY_HAT.get())
				.pattern("BBB").pattern("FOF").pattern("FUF")
				.define('U', GolemItems.EMPTY_UPGRADE)
				.define('O', GoetyCompatRegistry.REV_RING)
				.define('F', ModItems.UNHOLY_FABRIC.get())
				.define('B', ModItems.UNHOLY_BLOOD.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,
						GRCompatRegistry.UPGRADE_CURSE.get())::unlockedBy, ModItems.UNHOLY_HAT.get())
				.pattern(" F ").pattern("BOB").pattern(" U ")
				.define('U', GolemItems.EMPTY_UPGRADE)
				.define('O', GoetyCompatRegistry.REV_RING)
				.define('F', ModItems.UNHOLY_FABRIC.get())
				.define('B', ModItems.UNHOLY_BLOOD.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));
	}

	@Override
	public ConfigDataProvider getDataGen(DataGenerator gen) {
		return new GRConfigGen(gen);
	}

}
