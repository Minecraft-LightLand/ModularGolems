package dev.xkmc.modulargolems.content.recipe;

import dev.xkmc.l2core.serial.recipe.CustomSmithingBuilder;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
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

	public void save(RecipeOutput output, Identifier id) {
		super.save(output, ResourceKey.create(Registries.RECIPE, id));
	}

}
