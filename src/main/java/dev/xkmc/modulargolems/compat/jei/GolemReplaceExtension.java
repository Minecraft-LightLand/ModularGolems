package dev.xkmc.modulargolems.compat.jei;

import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.recipe.GolemReplaceRecipe;
import dev.xkmc.modulargolems.init.ModularGolems;
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
			} else if (inStack.getItem() instanceof GolemHolder<?, ?>) {
				setRecipeHolderIn(builder, craftingGridHelper, inStack);
				return;
			}
		}
		var out = focuses.getItemStackFocuses(RecipeIngredientRole.OUTPUT).findAny();
		if (out.isPresent()) {
			ItemStack outStack = out.get().getTypedValue().getIngredient();
			if (outStack.getItem() instanceof GolemHolder<?, ?>) {
				setRecipeHolderOut(builder, craftingGridHelper, outStack);
				return;

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
					if (GolemMaterialConfig.mayApply(part, rl)) {
						ItemStack stack = new ItemStack(part);
						list.add(GolemPart.setMaterial(stack, rl));
						outputs.add(recipe.assembleForJEI(rl));
					}
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

	private void setRecipeHolderOut(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, ItemStack focusStack) {
		List<List<ItemStack>> inputs = new ArrayList<>();
		var old = GolemHolder.getMaterial(focusStack);

		for (Ingredient ing : recipe.getIngredients()) {
			ItemStack[] stacks = ing.getItems();
			if (stacks.length == 1 && stacks[0].getItem() instanceof GolemPart<?, ?> part) {
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
			} else if (stacks.length == 1 && stacks[0].getItem() instanceof GolemHolder<?, ?> holder) {
				List<ItemStack> list = new ArrayList<>();
				for (var e : GolemMaterialConfig.get().getAllMaterials()) {
					if (GolemMaterialConfig.mayApply(holder, e)) {
						ItemStack stack = new ItemStack(holder);
						for (var part : holder.getEntityType().values()) {
							GolemHolder.addMaterial(stack, part.toItem(), old.get(part.ordinal()).id());
						}
						stack = recipe.assembleForJEI(e, stack);
						list.add(stack);
					}
				}
				inputs.add(list);
			} else inputs.add(List.of(stacks));
		}
		int width = getWidth();
		int height = getHeight();

		craftingGridHelper.createAndSetOutputs(builder, List.of(focusStack));
		craftingGridHelper.createAndSetInputs(builder, inputs, width, height);
	}


	private void setRecipeHolderIn(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, ItemStack focusStack) {
		List<List<ItemStack>> inputs = new ArrayList<>();
		List<ItemStack> out = new ArrayList<>();
		var old = GolemHolder.getMaterial(focusStack);
		ItemStack holderStack = new ItemStack(focusStack.getItem());
		for (var part : old) {
			GolemHolder.addMaterial(holderStack, part.getPart(), part.id());
		}
		for (Ingredient ing : recipe.getIngredients()) {
			ItemStack[] stacks = ing.getItems();
			if (stacks.length == 1 && stacks[0].getItem() instanceof GolemPart<?, ?> part) {
				List<ItemStack> in = new ArrayList<>();
				for (var e : GolemMaterialConfig.get().getAllMaterials()) {
					if (GolemMaterialConfig.mayApply(part, e)) {
						in.add(GolemPart.setMaterial(new ItemStack(part), e));
						out.add(recipe.assembleForJEI(e, holderStack));
					}
				}
				inputs.add(in);
			} else if (stacks.length == 1 && stacks[0].getItem() instanceof GolemHolder<?, ?>) {
				inputs.add(List.of(holderStack));
			} else inputs.add(List.of(stacks));
		}
		int width = getWidth();
		int height = getHeight();

		craftingGridHelper.createAndSetOutputs(builder, out);
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
