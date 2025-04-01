package dev.xkmc.modulargolems.content.recipe;

import dev.xkmc.l2library.serial.recipe.CustomShapedBuilder;
import dev.xkmc.modulargolems.init.registrate.GolemMiscs;
import net.minecraft.world.level.ItemLike;

public class GolemReplaceBuilder extends CustomShapedBuilder<GolemReplaceRecipe> {

	public GolemReplaceBuilder(ItemLike result, int count) {
		super(GolemMiscs.REPLACE, result, count);
	}
}
