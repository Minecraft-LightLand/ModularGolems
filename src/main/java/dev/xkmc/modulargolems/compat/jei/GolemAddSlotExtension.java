package dev.xkmc.modulargolems.compat.jei;

import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.upgrade.IUpgradeItem;
import dev.xkmc.modulargolems.content.recipe.GolemSmithAddSlotRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.smithing.ISmithingCategoryExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record GolemAddSlotExtension(
) implements ISmithingCategoryExtension<GolemSmithAddSlotRecipe> {

	@Override
	public void onDisplayedIngredientsUpdate(GolemSmithAddSlotRecipe r, IRecipeSlotDrawable templateSlot, IRecipeSlotDrawable baseSlot, IRecipeSlotDrawable additionSlot, IRecipeSlotDrawable outputSlot, IFocusGroup focuses) {
		var template = templateSlot.getDisplayedIngredient(VanillaTypes.ITEM_STACK).orElse(ItemStack.EMPTY);
		var base = baseSlot.getDisplayedIngredient(VanillaTypes.ITEM_STACK).orElse(ItemStack.EMPTY);
		var addition = additionSlot.getDisplayedIngredient(VanillaTypes.ITEM_STACK).orElse(ItemStack.EMPTY);

		ResourceLocation id = null;

		for (var e : GolemMaterialConfig.get().ingredients.entrySet()) {
			if (e.getValue().test(addition)) {
				id = e.getKey();
				break;
			}
		}

		if (id != null && template.getItem() instanceof IUpgradeItem item &&
				base.getItem() instanceof GolemHolder<?, ?> golem) {
			baseSlot.clearDisplayOverrides();
			outputSlot.clearDisplayOverrides();

			ItemStack baseGolem = new ItemStack(golem);
			for (var part : golem.getEntityType().values()) {
				GolemHolder.addMaterial(baseGolem, part.toItem(), id);
			}
			ItemStack holder = baseGolem.copy();
			GolemHolder.addUpgrade(holder, item);

			baseSlot.createDisplayOverrides().addItemStacks(List.of(baseGolem));
			outputSlot.createDisplayOverrides().addItemStacks(List.of(holder));
		}
	}

	@Override
	public <T extends IIngredientAcceptor<T>> void setTemplate(GolemSmithAddSlotRecipe r, T t) {
		t.addIngredients(r.template);
	}

	@Override
	public <T extends IIngredientAcceptor<T>> void setBase(GolemSmithAddSlotRecipe r, T t) {
		t.addIngredients(r.base);
	}

	@Override
	public <T extends IIngredientAcceptor<T>> void setAddition(GolemSmithAddSlotRecipe r, T t) {
		if (r.base.getItems()[0].getItem() instanceof GolemHolder<?, ?> golem) {
			List<ItemStack> list = new ArrayList<>();
			for (ResourceLocation rl : GolemMaterialConfig.get().getAllMaterials()) {
				if (!GolemMaterialConfig.mayApply(golem, rl)) continue;
				var mat = GolemMaterialConfig.get().ingredients.get(rl);
				list.addAll(List.of(mat.getItems()));
			}
			t.addItemStacks(list);
		} else t.addIngredients(r.addition);
	}

}
