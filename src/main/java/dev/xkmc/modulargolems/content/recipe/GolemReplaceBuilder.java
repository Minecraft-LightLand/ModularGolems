package dev.xkmc.modulargolems.content.recipe;

import dev.xkmc.l2core.serial.recipe.CustomShapedBuilder;
import net.minecraft.world.level.ItemLike;

public class GolemReplaceBuilder extends CustomShapedBuilder<GolemReplaceRecipe> {

	public GolemReplaceBuilder(ItemLike result, int count) {
		super(GolemReplaceRecipe::new, result, count);
	}
}
