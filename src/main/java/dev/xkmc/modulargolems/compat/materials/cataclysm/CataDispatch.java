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
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
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
		pvd.add("golem_material." + MODID + ".cursium", "Cursium");
	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {
		unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CataCompatRegistry.ENDER_GUARDIAN.get())::unlockedBy,
				ModItems.GAUNTLET_OF_GUARD.get()).requires(GolemItems.EMPTY_UPGRADE).requires(ModItems.GAUNTLET_OF_GUARD.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CataCompatRegistry.LEVIATHAN.get())::unlockedBy,
				ModItems.TIDAL_CLAWS.get()).requires(GolemItems.EMPTY_UPGRADE).requires(ModItems.TIDAL_CLAWS.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CataCompatRegistry.MONSTROSITY.get())::unlockedBy,
				ModItems.INFERNAL_FORGE.get()).requires(GolemItems.EMPTY_UPGRADE).requires(ModItems.INFERNAL_FORGE.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CataCompatRegistry.ANCIENT_REMNANT.get())::unlockedBy,
				ModItems.SANDSTORM_IN_A_BOTTLE.get()).requires(GolemItems.EMPTY_UPGRADE).requires(ModItems.SANDSTORM_IN_A_BOTTLE.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CataCompatRegistry.HARBINGER_TEMPLATE.get())::unlockedBy,
				ModItems.WITHERITE_INGOT.get())
				.pattern("ASA").pattern("ABA").pattern("ACA")
				.define('A', Items.REDSTONE_BLOCK)
				.define('B', Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
				.define('C', ModItems.WITHERITE_INGOT.get())
				.define('S', Items.NETHER_STAR)
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CataCompatRegistry.MONSTROSITY_TEMPLATE.get())::unlockedBy,
				ModItems.MONSTROUS_HORN.get())
				.pattern("ASA").pattern("ABA").pattern("ACA")
				.define('A', Blocks.GILDED_BLACKSTONE)
				.define('B', Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
				.define('C', ModItems.MONSTROUS_HORN.get())
				.define('S', Items.NETHER_STAR)
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		unlock(pvd, SmithingTransformRecipeBuilder.smithing(
						Ingredient.of(CataCompatRegistry.HARBINGER_TEMPLATE.get()),
						Ingredient.of(GolemItems.BARBARICFLAMEVANGUARD_HELMET.get()),
						Ingredient.of(ModItems.MONSTROUS_HORN.get()),
						RecipeCategory.COMBAT, CataCompatRegistry.MONSTROSITY_HELMET.get())::unlocks,
				GolemItems.BARBARICFLAMEVANGUARD_HELMET.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID), CataCompatRegistry.MONSTROSITY_HELMET.getId());

		unlock(pvd, SmithingTransformRecipeBuilder.smithing(
						Ingredient.of(CataCompatRegistry.HARBINGER_TEMPLATE.get()),
						Ingredient.of(GolemItems.BARBARICFLAMEVANGUARD_CHESTPLATE.get()),
						Ingredient.of(ModItems.MONSTROUS_HORN.get()),
						RecipeCategory.COMBAT, CataCompatRegistry.MONSTROSITY_CHESTPLATE.get())::unlocks,
				GolemItems.BARBARICFLAMEVANGUARD_CHESTPLATE.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID), CataCompatRegistry.MONSTROSITY_CHESTPLATE.getId());

		unlock(pvd, SmithingTransformRecipeBuilder.smithing(
						Ingredient.of(CataCompatRegistry.HARBINGER_TEMPLATE.get()),
						Ingredient.of(GolemItems.BARBARICFLAMEVANGUARD_SHINGUARD.get()),
						Ingredient.of(ModItems.MONSTROUS_HORN.get()),
						RecipeCategory.COMBAT, CataCompatRegistry.MONSTROSITY_SHINGUARD.get())::unlocks,
				GolemItems.BARBARICFLAMEVANGUARD_SHINGUARD.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID), CataCompatRegistry.MONSTROSITY_SHINGUARD.getId());
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
