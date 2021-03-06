package dev.xkmc.modulargolems.content.recipe;

import dev.xkmc.l2library.base.recipe.AbstractShapedRecipe;
import dev.xkmc.modulargolems.content.item.GolemHolder;
import dev.xkmc.modulargolems.content.item.GolemPart;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class GolemAssembleRecipe extends AbstractShapedRecipe<GolemAssembleRecipe> {

	public GolemAssembleRecipe(ResourceLocation id, String group, int w, int h, NonNullList<Ingredient> ings, ItemStack result) {
		super(id, group, w, h, ings, result);
	}

	@Override
	public ItemStack assemble(CraftingContainer cont) {
		ItemStack stack = super.assemble(cont);
		for (int i = 0; i < cont.getContainerSize(); i++) {
			ItemStack input = cont.getItem(i);
			if (!input.isEmpty() && input.getItem() instanceof GolemPart part) {
				GolemPart.getMaterial(input).ifPresent(mat -> GolemHolder.addMaterial(stack, part, mat));
			}
		}
		return stack;
	}

}
