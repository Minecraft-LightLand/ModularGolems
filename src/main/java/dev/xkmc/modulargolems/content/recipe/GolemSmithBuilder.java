package dev.xkmc.modulargolems.content.recipe;

import dev.xkmc.l2library.serial.recipe.CustomSmithingBuilder;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.init.registrate.GolemMiscs;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class GolemSmithBuilder extends CustomSmithingBuilder<GolemSmithAddSlotRecipe> {

	public GolemSmithBuilder(GolemHolder<?, ?> holder, ItemLike template) {
		super(GolemMiscs.SMITH_ADD_SLOT, Ingredient.of(template), Ingredient.of(holder), Ingredient.of(Items.IRON_INGOT), holder);
	}

}
