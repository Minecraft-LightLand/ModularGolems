package dev.xkmc.modulargolems.compat.jei;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2core.util.ContextHelper;
import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.item.data.GolemHolderMaterial;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.recipe.GolemAssembleRecipe;
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

public record GolemAssemblyExtension(
) implements ICraftingCategoryExtension<GolemAssembleRecipe> {

	@Override
	public List<SlotDisplay> getIngredients(RecipeHolder<GolemAssembleRecipe> recipeHolder) {
		return recipeHolder.value().getIngredients().stream()
				.map(e -> e.map(Ingredient::display).orElse(null))
				.filter(Objects::nonNull).toList();
	}

	@Override
	public void setRecipe(RecipeHolder<GolemAssembleRecipe> holder, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
		var out = focuses.getItemStackFocuses(RecipeIngredientRole.OUTPUT).findAny();
		if (out.isPresent()) {
			ItemStack outStack = out.get().getTypedValue().getIngredient();
			if (outStack.getItem() instanceof GolemHolder<?, ?> h &&
					GolemHolder.getMaterial(outStack).size() == h.getEntityType().values().length) {
				setRecipeSpecial(holder, builder, craftingGridHelper, outStack);
				return;
			}
		}
		setRecipeAll(holder, builder, craftingGridHelper, focuses);
	}

	private void setRecipeAll(RecipeHolder<GolemAssembleRecipe> holder, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
		List<List<ItemStack>> inputs = new ArrayList<>();
		for (var opt : holder.value().getIngredients()) {
			if (opt.isEmpty()) {
				inputs.add(List.of());
				continue;
			}
			var ing = opt.get();
			var stacks = ContextHelper.resolve(ing);
			if (stacks.size() == 1 && stacks.getFirst().getItem() instanceof GolemPart<?, ?> part) {
				List<ItemStack> list = new ArrayList<>();
				for (Identifier rl : GolemMaterialConfig.get().getAllMaterials()) {
					ItemStack stack = new ItemStack(part);
					list.add(GolemPart.setMaterial(stack, rl));
				}
				inputs.add(list);
			} else {
				inputs.add(stacks);
			}
		}
		ItemStack resultItem = holder.value().getResult();
		List<ItemStack> list = new ArrayList<>();
		if (resultItem.getItem() instanceof GolemHolder<?, ?> golem) {
			Pair<GolemPart<?, ?>, Identifier> fix = null;
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
			for (Identifier rl : GolemMaterialConfig.get().getAllMaterials()) {
				ItemStack stack = new ItemStack(golem);
				ArrayList<GolemHolderMaterial.Entry> mats = new ArrayList<>();
				for (var part : golem.getEntityType().values()) {
					GolemPart<?, ?> partItem = part.toItem();
					if (fix != null && fix.getFirst() == partItem) {
						mats.add(new GolemHolderMaterial.Entry(partItem, fix.getSecond()));
					} else {
						mats.add(new GolemHolderMaterial.Entry(partItem, rl));
					}
				}
				list.add(GolemItems.HOLDER_MAT.set(stack, new GolemHolderMaterial(mats)));
			}
		} else {
			list.add(resultItem);
		}
		int width = getWidth(holder);
		int height = getHeight(holder);
		craftingGridHelper.createAndSetOutputs(builder, list);
		craftingGridHelper.createAndSetInputs(builder, inputs, width, height);
	}

	private void setRecipeSpecial(RecipeHolder<GolemAssembleRecipe> holder, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, ItemStack focusResult) {
		var mats = GolemHolder.getMaterial(focusResult);
		List<List<ItemStack>> inputs = new ArrayList<>();
		int ind = 0;
		for (var opt : holder.value().getIngredients()) {
			if (opt.isEmpty()) {
				inputs.add(List.of());
				continue;
			}
			var ing = opt.get();
			var stacks = ContextHelper.resolve(ing);
			if (stacks.size() == 1 && stacks.getFirst().getItem() instanceof GolemPart<?, ?> part) {
				GolemMaterial mat = mats.get(ind++);
				inputs.add(List.of(GolemPart.setMaterial(new ItemStack(part), mat.id())));
			} else inputs.add(stacks);

		}
		int width = getWidth(holder);
		int height = getHeight(holder);
		craftingGridHelper.createAndSetOutputs(builder, List.of(focusResult));
		craftingGridHelper.createAndSetInputs(builder, inputs, width, height);
	}

	@Override
	public int getWidth(RecipeHolder<GolemAssembleRecipe> recipeHolder) {
		return recipeHolder.value().getWidth();
	}

	@Override
	public int getHeight(RecipeHolder<GolemAssembleRecipe> recipeHolder) {
		return recipeHolder.value().getHeight();
	}

}
