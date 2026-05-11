package dev.xkmc.modulargolems.content.recipe;

import dev.xkmc.l2core.serial.recipe.CustomSmithingBuilder;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class GolemSmithBuilder extends CustomSmithingBuilder<GolemSmithAddSlotRecipe> {

	public GolemSmithBuilder(HolderGetter<Item> pvd, GolemHolder<?, ?> holder, TagKey<Item> template) {
		super(GolemSmithAddSlotRecipe::new, Ingredient.of(pvd.getOrThrow(template)), Ingredient.of(holder), Ingredient.of(Items.IRON_INGOT),
				RecipeCategory.COMBAT, new ItemStackTemplate(holder));
	}

}
