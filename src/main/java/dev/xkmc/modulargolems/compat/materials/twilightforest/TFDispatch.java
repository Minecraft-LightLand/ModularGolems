package dev.xkmc.modulargolems.compat.materials.twilightforest;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.l2library.serial.ingredients.EnchantmentIngredient;
import dev.xkmc.l2library.serial.recipe.ConditionalRecipeWrapper;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import twilightforest.init.TFBlocks;
import twilightforest.init.TFItems;

public class TFDispatch extends ModDispatch {

	public static final String MODID = "twilightforest";

	public TFDispatch() {
		TFCompatRegistry.register();
	}

	public void genLang(RegistrateLangProvider pvd) {
		pvd.add("golem_material." + MODID + ".ironwood", "Ironwood");
		pvd.add("golem_material." + MODID + ".steeleaf", "Steeleaf");
		pvd.add("golem_material." + MODID + ".knightmetal", "Knightmetal");
		pvd.add("golem_material." + MODID + ".fiery", "Fiery");
	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {
		safeUpgrade(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TFCompatRegistry.UP_CARMINITE.get())::unlockedBy, TFItems.CARMINITE.get())
				.pattern("CAC").pattern("ABA").pattern("CAC")
				.define('A', TFItems.CARMINITE.get())
				.define('B', GolemItems.EMPTY_UPGRADE.get())
				.define('C', TFBlocks.ENCASED_TOWERWOOD.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		safeUpgrade(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TFCompatRegistry.UP_FIERY.get())::unlockedBy, TFItems.FIERY_INGOT.get())
				.pattern("CAC").pattern("ABA").pattern("CAC")
				.define('A', TFItems.FIERY_INGOT.get())
				.define('B', GolemItems.EMPTY_UPGRADE.get())
				.define('C', Items.BLAZE_POWDER)
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		safeUpgrade(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TFCompatRegistry.UP_KNIGHTMETAL.get())::unlockedBy, TFItems.KNIGHTMETAL_INGOT.get())
				.pattern("CAC").pattern("ABA").pattern("CAC")
				.define('A', TFItems.KNIGHTMETAL_INGOT.get())
				.define('B', GolemItems.EMPTY_UPGRADE.get())
				.define('C', TFBlocks.HEDGE.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		safeUpgrade(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TFCompatRegistry.UP_STEELEAF.get())::unlockedBy, TFItems.STEELEAF_INGOT.get())
				.pattern(" A ").pattern("ABA").pattern(" A ")
				.define('A', TFItems.STEELEAF_INGOT.get())
				.define('B', GolemItems.EMPTY_UPGRADE.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		safeUpgrade(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TFCompatRegistry.UP_IRONWOOD.get())::unlockedBy, TFItems.IRONWOOD_INGOT.get())
				.pattern(" A ").pattern("ABA").pattern(" A ")
				.define('A', TFItems.IRONWOOD_INGOT.get())
				.define('B', GolemItems.EMPTY_UPGRADE.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		safeUpgrade(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TFCompatRegistry.UP_NAGA.get())::unlockedBy, TFItems.NAGA_SCALE.get())
				.pattern(" A ").pattern("ABA").pattern(" A ")
				.define('A', TFItems.NAGA_SCALE.get())
				.define('B', GolemItems.EMPTY_UPGRADE.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));


		safeUpgrade(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GolemItems.RECYCLE.get())::unlockedBy, TFItems.CHARM_OF_LIFE_2.get())
				.pattern(" A ").pattern("EBE").pattern(" R ")
				.define('A', Ingredient.of(TFItems.CHARM_OF_LIFE_1.get(), TFItems.CHARM_OF_LIFE_2.get()))
				.define('B', GolemItems.EMPTY_UPGRADE.get())
				.define('E', Items.ENDER_PEARL)
				.define('R', Blocks.RESPAWN_ANCHOR)
				.save(ConditionalRecipeWrapper.mod(pvd, MODID), ModularGolems.loc("recycle_upgrade_from_life_charm"));

		safeUpgrade(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, GolemItems.RECYCLE.get())::unlockedBy, TFItems.CHARM_OF_KEEPING_3.get())
				.requires(GolemItems.EMPTY_UPGRADE.get()).requires(TFItems.CHARM_OF_KEEPING_3.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID), ModularGolems.loc("recycle_upgrade_from_lock_charm_3"));

		safeUpgrade(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GolemItems.RECYCLE.get())::unlockedBy, TFItems.CHARM_OF_KEEPING_2.get())
				.pattern(" A ").pattern("1B2").pattern(" 3 ")
				.define('A', TFItems.CHARM_OF_KEEPING_2.get())
				.define('B', GolemItems.EMPTY_UPGRADE.get())
				.define('1', new EnchantmentIngredient(Enchantments.INFINITY_ARROWS, 1))
				.define('2', new EnchantmentIngredient(Enchantments.MENDING, 1))
				.define('3', new EnchantmentIngredient(Enchantments.UNBREAKING, 3))
				.save(ConditionalRecipeWrapper.mod(pvd, MODID), ModularGolems.loc("recycle_upgrade_from_lock_charm_2"));
	}

	@Override
	public ConfigDataProvider getDataGen(DataGenerator gen) {
		return new TFConfigGen(gen);
	}

}
