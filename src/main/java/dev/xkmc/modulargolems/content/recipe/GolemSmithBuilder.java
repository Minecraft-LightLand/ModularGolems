package dev.xkmc.modulargolems.content.recipe;

import dev.xkmc.l2core.serial.recipe.CustomSmithingBuilder;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class GolemSmithBuilder extends CustomSmithingBuilder<GolemSmithAddSlotRecipe> {

	public GolemSmithBuilder(GolemHolder<?, ?> holder, TagKey<Item> template) {
		super(GolemSmithAddSlotRecipe::new, Ingredient.of(template), Ingredient.of(holder), Ingredient.of(Items.IRON_INGOT), holder);
	}

}
