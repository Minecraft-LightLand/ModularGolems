package dev.xkmc.modulargolems.compat.jei;

import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.recipe.GolemReplaceRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public record GolemReplaceExtension(GolemReplaceRecipe recipe) implements ICraftingCategoryExtension {

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
		var in = focuses.getItemStackFocuses(RecipeIngredientRole.INPUT).findAny();
		if (in.isPresent()) {
			ItemStack inStack = in.get().getTypedValue().getIngredient();
			if (inStack.getItem() instanceof GolemPart<?, ?>) {
				var opt = GolemPart.getMaterial(inStack);
				if (opt.isPresent()) {
					setRecipeSpecial(builder, craftingGridHelper, inStack, opt.get());
					return;
				}
			}
		}
		setRecipeAll(builder, craftingGridHelper, focuses);
	}

	private void setRecipeAll(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
		List<List<ItemStack>> inputs = new ArrayList<>();
		List<ItemStack> outputs = new ArrayList<>();
		for (Ingredient ing : recipe.getIngredients()) {
			ItemStack[] stacks = ing.getItems();
			if (stacks.length == 1 && stacks[0].getItem() instanceof GolemPart<?, ?> part) {
				List<ItemStack> list = new ArrayList<>();
				for (ResourceLocation rl : GolemMaterialConfig.get().getAllMaterials()) {
					ItemStack stack = new ItemStack(part);
					list.add(GolemPart.setMaterial(stack, rl));
					outputs.add(recipe.assembleForJEI(rl));
				}
				inputs.add(list);
			} else {
				inputs.add(List.of(stacks));
			}
		}
		int width = getWidth();
		int height = getHeight();
		craftingGridHelper.createAndSetOutputs(builder, outputs);
		craftingGridHelper.createAndSetInputs(builder, inputs, width, height);
	}

	private void setRecipeSpecial(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, ItemStack focusResult, ResourceLocation mat) {
		List<List<ItemStack>> inputs = new ArrayList<>();
		for (Ingredient ing : recipe.getIngredients()) {
			ItemStack[] stacks = ing.getItems();
			if (stacks.length == 1 && stacks[0].getItem() instanceof GolemPart<?, ?>) {
				inputs.add(List.of(focusResult));
			} else inputs.add(List.of(stacks));
		}
		int width = getWidth();
		int height = getHeight();
		craftingGridHelper.createAndSetOutputs(builder, List.of(recipe.assembleForJEI(mat)));
		craftingGridHelper.createAndSetInputs(builder, inputs, width, height);
	}

	@Override
	public int getWidth() {
		return recipe.getWidth();
	}

	@Override
	public int getHeight() {
		return recipe.getHeight();
	}

}
