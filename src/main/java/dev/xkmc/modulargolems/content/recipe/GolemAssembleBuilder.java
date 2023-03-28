package dev.xkmc.modulargolems.content.recipe;

import dev.xkmc.l2library.base.recipe.CustomShapedBuilder;
import dev.xkmc.modulargolems.init.registrate.GolemMiscs;
import net.minecraft.world.level.ItemLike;

public class GolemAssembleBuilder extends CustomShapedBuilder<GolemAssembleRecipe> {

	public GolemAssembleBuilder(ItemLike result, int count) {
		super(GolemMiscs.ASSEMBLE, result, count);
	}
}
