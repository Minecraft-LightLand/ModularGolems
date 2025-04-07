package dev.xkmc.modulargolems.compat.materials.allthemodium;

import com.thevortex.allthemodium.registry.ModRegistry;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.l2core.serial.config.ConfigDataProvider;
import dev.xkmc.l2core.serial.recipe.ConditionalRecipeWrapper;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.recipe.GolemSmithBuilder;
import dev.xkmc.modulargolems.init.data.RecipeGen;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ATMDispatch extends ModDispatch {

	public static final String MODID = "allthemodium";

	public ATMDispatch() {
		ATMCompatRegistry.register();
	}

	@Override
	protected void genLang(RegistrateLangProvider pvd) {
		pvd.add("golem_material." + MODID + ".allthemodium", "Allthemodium");
		pvd.add("golem_material." + MODID + ".vibranium", "Vibranium");
		pvd.add("golem_material." + MODID + ".unobtainium", "Unobtainium");
	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATMCompatRegistry.EX_ATM, 1)::unlockedBy,
						ModRegistry.ALLTHEMODIUM_INGOT.get())
				.pattern("ACA").pattern("ABA").pattern("DDD")
				.define('A', GolemItems.EMPTY_UPGRADE)
				.define('B', ModRegistry.ATM_SMITHING.get())
				.define('C', ModRegistry.ALLTHEMODIUM_INGOT.get())
				.define('D', GolemItems.ADD_NETHERITE)
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATMCompatRegistry.EX_VIB, 1)::unlockedBy,
						ModRegistry.VIBRANIUM_INGOT.get())
				.pattern("ACA").pattern("ABA").pattern("DED")
				.define('A', GolemItems.EMPTY_UPGRADE)
				.define('B', ModRegistry.VIB_SMITHING.get())
				.define('C', ModRegistry.VIBRANIUM_INGOT.get())
				.define('D', GolemItems.ADD_NETHERITE)
				.define('E', ATMCompatRegistry.EX_ATM)
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATMCompatRegistry.EX_UNO, 1)::unlockedBy,
						ModRegistry.VIBRANIUM_INGOT.get())
				.pattern("ACA").pattern("ABA").pattern("DED")
				.define('A', GolemItems.EMPTY_UPGRADE)
				.define('B', ModRegistry.UNO_SMITHING.get())
				.define('C', ModRegistry.UNOBTAINIUM_INGOT.get())
				.define('D', GolemItems.ADD_NETHERITE)
				.define('E', ATMCompatRegistry.EX_VIB)
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

	}

	@Override
	public @Nullable ConfigDataProvider getDataGen(DataGenerator gen, CompletableFuture<HolderLookup.Provider> pvd) {
		return new ATMConfigGen(gen, pvd);
	}

}
