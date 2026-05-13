package dev.xkmc.modulargolems.compat.jei;

import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.recipe.GolemReplaceRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record GolemReplaceExtension()
		implements ICraftingCategoryExtension<GolemReplaceRecipe> {

	@Override
	public List<SlotDisplay> getIngredients(RecipeHolder<GolemReplaceRecipe> recipeHolder) {
		return recipeHolder.value().getIngredients().stream()
				.map(e -> e.map(Ingredient::display).orElse(null))
				.filter(Objects::nonNull).toList();
	}

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
		for (var opt : recipe.getIngredients()) {
			if (opt.isEmpty()) {
				inputs.add(List.of());
				continue;
			}
			var ing = opt.get();
			List<ItemStack> stacks = ing.display().resolveForStacks(ContextMap.EMPTY);
			if (stacks.size() == 1 && stacks.getFirst().getItem() instanceof GolemPart<?, ?> part) {
				List<ItemStack> list = new ArrayList<>();
				for (Identifier rl : GolemMaterialConfig.get().getAllMaterials()) {
					ItemStack stack = new ItemStack(part);
					list.add(GolemPart.setMaterial(stack, rl));
					outputs.add(recipe.assembleForJEI(rl));
				}
				inputs.add(list);
			} else {
				inputs.add(stacks);
			}
		}
		int width = getWidth(holder);
		int height = getHeight(holder);
		craftingGridHelper.createAndSetOutputs(builder, outputs);
		craftingGridHelper.createAndSetInputs(builder, inputs, width, height);
	}

	private void setRecipeSpecial(RecipeHolder<GolemReplaceRecipe> holder, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, ItemStack focusResult, Identifier mat) {
		GolemReplaceRecipe recipe = holder.value();
		List<List<ItemStack>> inputs = new ArrayList<>();
		for (var opt : recipe.getIngredients()) {
			if (opt.isEmpty()) {
				inputs.add(List.of());
				continue;
			}
			var ing = opt.get();
			var stacks = ing.display().resolveForStacks(ContextMap.EMPTY);
			if (stacks.size() == 1 && stacks.getFirst().getItem() instanceof GolemPart<?, ?>) {
				inputs.add(List.of(focusResult));
			} else inputs.add(stacks);
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
