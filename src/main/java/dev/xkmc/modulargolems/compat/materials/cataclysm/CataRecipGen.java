package dev.xkmc.modulargolems.compat.materials.cataclysm;

import com.github.L_Ender.cataclysm.init.ModBlocks;
import com.github.L_Ender.cataclysm.init.ModItems;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2core.serial.recipe.ConditionalRecipeWrapper;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

import static dev.xkmc.modulargolems.init.data.RecipeGen.unlock;

public class CataRecipGen {

	public static void genRecipe(RegistrateRecipeProvider pvd) {
		var output = ConditionalRecipeWrapper.mod(pvd, CataDispatch.MODID);

		unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CataCompatRegistry.ENDER_GUARDIAN.get())::unlockedBy,
				ModItems.GAUNTLET_OF_GUARD.get()).requires(GolemItems.EMPTY_UPGRADE).requires(ModItems.GAUNTLET_OF_GUARD.get())
				.save(output);

		unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CataCompatRegistry.LEVIATHAN.get())::unlockedBy,
				ModItems.TIDAL_CLAWS.get()).requires(GolemItems.EMPTY_UPGRADE).requires(ModItems.TIDAL_CLAWS.get())
				.save(output);

		unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CataCompatRegistry.MONSTROSITY.get())::unlockedBy,
				ModItems.INFERNAL_FORGE.get()).requires(GolemItems.EMPTY_UPGRADE).requires(ModItems.INFERNAL_FORGE.get())
				.save(output);

		unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CataCompatRegistry.ANCIENT_REMNANT.get())::unlockedBy,
				ModItems.SANDSTORM_IN_A_BOTTLE.get()).requires(GolemItems.EMPTY_UPGRADE).requires(ModItems.SANDSTORM_IN_A_BOTTLE.get())
				.save(output);

		unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CataCompatRegistry.HARBINGER_TEMPLATE.get())::unlockedBy,
				ModItems.WITHERITE_INGOT.get())
				.pattern("ASA").pattern("ABA").pattern("ACA")
				.define('A', Items.REDSTONE_BLOCK)
				.define('B', Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
				.define('C', ModItems.WITHERITE_INGOT.get())
				.define('S', Items.NETHER_STAR)
				.save(output);

		unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CataCompatRegistry.MONSTROSITY_TEMPLATE.get())::unlockedBy,
				ModItems.MONSTROUS_HORN.get())
				.pattern("AGA").pattern("ABA").pattern("ACA")
				.define('A', Blocks.BLACKSTONE)
				.define('G', Items.GOLD_INGOT)
				.define('B', Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
				.define('C', ModItems.LAVA_POWER_CELL.get())
				.save(output);

		unlock(pvd, SmithingTransformRecipeBuilder.smithing(
						Ingredient.of(CataCompatRegistry.HARBINGER_TEMPLATE.get()),
						Ingredient.of(GolemItems.BARBARICFLAMEVANGUARD_HELMET.get()),
						Ingredient.of(ModBlocks.WITHERITE_BLOCK.get()),
						RecipeCategory.COMBAT, CataCompatRegistry.HARBINGER_HELMET.get())::unlocks,
				GolemItems.BARBARICFLAMEVANGUARD_HELMET.get())
				.save(output, CataCompatRegistry.HARBINGER_HELMET.getId());

		unlock(pvd, SmithingTransformRecipeBuilder.smithing(
						Ingredient.of(CataCompatRegistry.HARBINGER_TEMPLATE.get()),
						Ingredient.of(GolemItems.BARBARICFLAMEVANGUARD_CHESTPLATE.get()),
						Ingredient.of(ModBlocks.WITHERITE_BLOCK.get()),
						RecipeCategory.COMBAT, CataCompatRegistry.HARBINGER_CHESTPLATE.get())::unlocks,
				GolemItems.BARBARICFLAMEVANGUARD_CHESTPLATE.get())
				.save(output, CataCompatRegistry.HARBINGER_CHESTPLATE.getId());

		unlock(pvd, SmithingTransformRecipeBuilder.smithing(
						Ingredient.of(CataCompatRegistry.HARBINGER_TEMPLATE.get()),
						Ingredient.of(GolemItems.BARBARICFLAMEVANGUARD_SHINGUARD.get()),
						Ingredient.of(ModItems.WITHERITE_INGOT.get()),
						RecipeCategory.COMBAT, CataCompatRegistry.HARBINGER_SHINGUARD.get())::unlocks,
				GolemItems.BARBARICFLAMEVANGUARD_SHINGUARD.get())
				.save(output, CataCompatRegistry.HARBINGER_SHINGUARD.getId());

		unlock(pvd, SmithingTransformRecipeBuilder.smithing(
						Ingredient.of(CataCompatRegistry.MONSTROSITY_TEMPLATE.get()),
						Ingredient.of(GolemItems.BARBARICFLAMEVANGUARD_HELMET.get()),
						Ingredient.of(ModItems.MONSTROUS_HORN.get()),
						RecipeCategory.COMBAT, CataCompatRegistry.MONSTROSITY_HELMET.get())::unlocks,
				GolemItems.BARBARICFLAMEVANGUARD_HELMET.get())
				.save(output, CataCompatRegistry.MONSTROSITY_HELMET.getId());

		unlock(pvd, SmithingTransformRecipeBuilder.smithing(
						Ingredient.of(CataCompatRegistry.MONSTROSITY_TEMPLATE.get()),
						Ingredient.of(GolemItems.BARBARICFLAMEVANGUARD_CHESTPLATE.get()),
						Ingredient.of(ModItems.MONSTROUS_HORN.get()),
						RecipeCategory.COMBAT, CataCompatRegistry.MONSTROSITY_CHESTPLATE.get())::unlocks,
				GolemItems.BARBARICFLAMEVANGUARD_CHESTPLATE.get())
				.save(output, CataCompatRegistry.MONSTROSITY_CHESTPLATE.getId());

		unlock(pvd, SmithingTransformRecipeBuilder.smithing(
						Ingredient.of(CataCompatRegistry.MONSTROSITY_TEMPLATE.get()),
						Ingredient.of(GolemItems.BARBARICFLAMEVANGUARD_SHINGUARD.get()),
						Ingredient.of(ModItems.MONSTROUS_HORN.get()),
						RecipeCategory.COMBAT, CataCompatRegistry.MONSTROSITY_SHINGUARD.get())::unlocks,
				GolemItems.BARBARICFLAMEVANGUARD_SHINGUARD.get())
				.save(output, CataCompatRegistry.MONSTROSITY_SHINGUARD.getId());
	}
}
