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
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

public record GolemReplaceExtension()
		implements ICraftingCategoryExtension<GolemReplaceRecipe> {

	@Override
	public void setRecipe(RecipeHolder<GolemReplaceRecipe> holder, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
		var in = focuses.getItemStackFocuses(RecipeIngredientRole.INPUT).findAny();
		if (in.isPresent()) {
			ItemStack inStack = in.get().getTypedValue().getIngredient();
			if (inStack.getItem() instanceof GolemPart<?, ?>) {
				var opt = GolemPart.getMaterial(inStack);
				if (opt.isPresent()) {
					setRecipeSpecial(holder, builder, craftingGridHelper, inStack, opt.get());
					return;
				}
			}
		}
		setRecipeAll(holder, builder, craftingGridHelper, focuses);
	}

	private void setRecipeAll(RecipeHolder<GolemReplaceRecipe> holder, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
		GolemReplaceRecipe recipe = holder.value();
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
		int width = getWidth(holder);
		int height = getHeight(holder);
		craftingGridHelper.createAndSetOutputs(builder, outputs);
		craftingGridHelper.createAndSetInputs(builder, inputs, width, height);
	}

	private void setRecipeSpecial(RecipeHolder<GolemReplaceRecipe> holder, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, ItemStack focusResult, ResourceLocation mat) {
		GolemReplaceRecipe recipe = holder.value();
		List<List<ItemStack>> inputs = new ArrayList<>();
		for (Ingredient ing : recipe.getIngredients()) {
			ItemStack[] stacks = ing.getItems();
			if (stacks.length == 1 && stacks[0].getItem() instanceof GolemPart<?, ?>) {
				inputs.add(List.of(focusResult));
			} else inputs.add(List.of(stacks));
		}
		int width = getWidth(holder);
		int height = getHeight(holder);
		craftingGridHelper.createAndSetOutputs(builder, List.of(recipe.assembleForJEI(mat)));
		craftingGridHelper.createAndSetInputs(builder, inputs, width, height);
	}

	@Override
	public int getWidth(RecipeHolder<GolemReplaceRecipe> recipeHolder) {
		return recipeHolder.value().getWidth();
	}

	@Override
	public int getHeight(RecipeHolder<GolemReplaceRecipe> recipeHolder) {
		return recipeHolder.value().getHeight();
	}

}
