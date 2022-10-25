package dev.xkmc.modulargolems.compat.jei;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.recipe.GolemAssembleRecipe;
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

public record GolemAssemblyExtension(GolemAssembleRecipe recipe) implements ICraftingCategoryExtension {

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
		var out = focuses.getItemStackFocuses(RecipeIngredientRole.OUTPUT).findAny();
		if (out.isPresent()) {
			ItemStack outStack = out.get().getTypedValue().getIngredient();
			setRecipeSpecial(builder, craftingGridHelper, outStack);
		} else setRecipeAll(builder, craftingGridHelper, focuses);
	}

	private void setRecipeAll(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
		List<List<ItemStack>> inputs = new ArrayList<>();
		for (Ingredient ing : recipe.getIngredients()) {
			ItemStack[] stacks = ing.getItems();
			if (stacks.length == 1 && stacks[0].getItem() instanceof GolemPart<?, ?> part) {
				List<ItemStack> list = new ArrayList<>();
				for (ResourceLocation rl : GolemMaterialConfig.get().getAllMaterials()) {
					ItemStack stack = new ItemStack(part);
					list.add(GolemPart.setMaterial(stack, rl));
				}
				inputs.add(list);
			} else {
				inputs.add(List.of(stacks));
			}
		}
		ItemStack resultItem = recipe.getResultItem();
		List<ItemStack> list = new ArrayList<>();
		if (resultItem.getItem() instanceof GolemHolder<?, ?> holder) {
			Pair<GolemPart<?, ?>, ResourceLocation> fix = null;
			var focus = focuses.getItemStackFocuses(RecipeIngredientRole.INPUT).findAny();
			if (focus.isPresent()) {
				ItemStack stack = focus.get().getTypedValue().getIngredient();
				if (stack.getItem() instanceof GolemPart<?, ?> part) {
					var mat = GolemPart.getMaterial(stack);
					if (mat.isPresent()) {
						fix = Pair.of(part, mat.get());
					}
				}
			}
			for (ResourceLocation rl : GolemMaterialConfig.get().getAllMaterials()) {
				ItemStack stack = new ItemStack(holder);
				for (var part : holder.getEntityType().values()) {
					GolemPart<?, ?> partItem = part.toItem();
					if (fix != null && fix.getFirst() == partItem) {
						GolemHolder.addMaterial(stack, partItem, fix.getSecond());
					} else {
						GolemHolder.addMaterial(stack, partItem, rl);
					}
				}
				list.add(stack);
			}
		} else {
			list.add(resultItem);
		}
		int width = getWidth();
		int height = getHeight();
		craftingGridHelper.createAndSetOutputs(builder, list);
		craftingGridHelper.createAndSetInputs(builder, inputs, width, height);
	}

	private void setRecipeSpecial(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, ItemStack focusResult) {
		var mats = GolemHolder.getMaterial(focusResult);
		List<List<ItemStack>> inputs = new ArrayList<>();
		int ind = 0;
		for (Ingredient ing : recipe.getIngredients()) {
			ItemStack[] stacks = ing.getItems();
			if (stacks.length == 1 && stacks[0].getItem() instanceof GolemPart<?, ?> part) {
				GolemMaterial mat = mats.get(ind++);
				inputs.add(List.of(GolemPart.setMaterial(new ItemStack(part), mat.id())));
			} else inputs.add(List.of(stacks));

		}
		int width = getWidth();
		int height = getHeight();
		craftingGridHelper.createAndSetOutputs(builder, List.of(focusResult));
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
