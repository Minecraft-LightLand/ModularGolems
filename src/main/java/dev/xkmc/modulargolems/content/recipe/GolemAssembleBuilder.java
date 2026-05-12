package dev.xkmc.modulargolems.content.recipe;

import dev.xkmc.l2core.serial.recipe.CustomShapedBuilder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;

public class GolemAssembleBuilder extends CustomShapedBuilder<GolemAssembleRecipe> {

	public GolemAssembleBuilder(HolderGetter<Item> pvd, ItemLike result) {
		super(GolemAssembleRecipe::new, pvd, RecipeCategory.COMBAT, new ItemStackTemplate(result.asItem()));
	}

	public void save(RecipeOutput output, Identifier id) {
		super.save(output, ResourceKey.create(Registries.RECIPE, id));
	}

}
