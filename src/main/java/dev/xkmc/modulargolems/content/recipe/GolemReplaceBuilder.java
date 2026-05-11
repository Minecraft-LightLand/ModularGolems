package dev.xkmc.modulargolems.content.recipe;

import dev.xkmc.l2core.serial.recipe.CustomShapedBuilder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;

public class GolemReplaceBuilder extends CustomShapedBuilder<GolemReplaceRecipe> {

	public GolemReplaceBuilder(HolderGetter<Item> pvd, ItemLike result) {
		super(GolemReplaceRecipe::new, pvd, RecipeCategory.COMBAT, new ItemStackTemplate(result.asItem()));
	}

}
