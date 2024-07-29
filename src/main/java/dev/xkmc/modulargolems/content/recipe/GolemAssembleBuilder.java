package dev.xkmc.modulargolems.content.recipe;

import dev.xkmc.l2core.serial.recipe.CustomShapedBuilder;
import net.minecraft.world.level.ItemLike;

public class GolemAssembleBuilder extends CustomShapedBuilder<GolemAssembleRecipe> {

	public GolemAssembleBuilder(ItemLike result, int count) {
		super(GolemAssembleRecipe::new, result, count);
	}
}
