package dev.xkmc.modulargolems.compat.jei;

import dev.xkmc.l2core.util.ContextHelper;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.recipe.GolemReplaceRecipe;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.resources.Identifier;
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
			} else if (inStack.getItem() instanceof GolemHolder<?, ?>) {
				setRecipeHolderIn(holder, builder, craftingGridHelper, inStack);
				return;
			}
		}
		var out = focuses.getItemStackFocuses(RecipeIngredientRole.OUTPUT).findAny();
		if (out.isPresent()) {
			ItemStack outStack = out.get().getTypedValue().getIngredient();
			if (outStack.getItem() instanceof GolemHolder<?, ?>) {
				setRecipeHolderOut(holder, builder, craftingGridHelper, outStack);
				return;
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
			List<ItemStack> stacks = ContextHelper.resolve(ing);
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

	private void setRecipeHolderOut(RecipeHolder<GolemReplaceRecipe> recipeHolder, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, ItemStack focusStack) {
		var recipe = recipeHolder.value();
		List<List<ItemStack>> inputs = new ArrayList<>();
		var old = GolemHolder.getMaterial(focusStack);

		ItemStack holderStack = new ItemStack(focusStack.getItem());
		var prev = GolemItems.HOLDER_MAT.get(focusStack);
		if (prev != null)
			GolemItems.HOLDER_MAT.set(holderStack, prev);


		for (var opt : recipe.getIngredients()) {
			if (opt.isEmpty()) {
				inputs.add(List.of());
				continue;
			}
			var ing = opt.get();
			var stacks = ContextHelper.resolve(ing);
			if (stacks.size() == 1 && stacks.getFirst().getItem() instanceof GolemPart<?, ?> part) {
				var dummyId = ModularGolems.loc("dummy");
				var dummy = recipe.assembleForJEI(dummyId);
				var dummyList = GolemHolder.getMaterial(dummy);
				int index = 0;
				for (int i = 0; i < dummyList.size(); i++) {
					if (dummyList.get(i).id().equals(dummyId)) {
						index = i;
						break;
					}
				}
				inputs.add(List.of(GolemPart.setMaterial(part.getDefaultInstance(), old.get(index).id())));
			} else if (stacks.size() == 1 && stacks.getFirst().getItem() instanceof GolemHolder<?, ?> holder) {
				List<ItemStack> list = new ArrayList<>();
				for (var e : GolemMaterialConfig.get().getAllMaterials()) {
					if (GolemMaterialConfig.mayApply(holder, e)) {
						list.add(recipe.assembleForJEI(e, holderStack));
					}
				}
				inputs.add(list);
			} else inputs.add(stacks);
		}
		int width = getWidth(recipeHolder);
		int height = getHeight(recipeHolder);

		craftingGridHelper.createAndSetOutputs(builder, List.of(holderStack));
		craftingGridHelper.createAndSetInputs(builder, inputs, width, height);
	}

	private void setRecipeHolderIn(RecipeHolder<GolemReplaceRecipe> recipeHolder, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, ItemStack focusStack) {
		var recipe = recipeHolder.value();
		List<List<ItemStack>> inputs = new ArrayList<>();
		List<ItemStack> out = new ArrayList<>();
		ItemStack holderStack = new ItemStack(focusStack.getItem());
		var prev = GolemItems.HOLDER_MAT.get(focusStack);
		if (prev != null)
			GolemItems.HOLDER_MAT.set(holderStack, prev);

		for (var opt : recipe.getIngredients()) {
			if (opt.isEmpty()) {
				inputs.add(List.of());
				continue;
			}
			var ing = opt.get();
			var stacks = ContextHelper.resolve(ing);
			if (stacks.size() == 1 && stacks.getFirst().getItem() instanceof GolemPart<?, ?> part) {
				List<ItemStack> in = new ArrayList<>();
				for (var e : GolemMaterialConfig.get().getAllMaterials()) {
					if (GolemMaterialConfig.mayApply(part, e)) {
						in.add(GolemPart.setMaterial(new ItemStack(part), e));
						out.add(recipe.assembleForJEI(e, holderStack));
					}
				}
				inputs.add(in);
			} else if (stacks.size() == 1 && stacks.getFirst().getItem() instanceof GolemHolder<?, ?>) {
				inputs.add(List.of(holderStack));
			} else inputs.add(stacks);
		}
		int width = getWidth(recipeHolder);
		int height = getHeight(recipeHolder);

		craftingGridHelper.createAndSetOutputs(builder, out);
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
			var stacks = ContextHelper.resolve(ing);
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
