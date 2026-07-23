package dev.xkmc.modulargolems.compat.jei;

import dev.xkmc.l2core.util.ContextHelper;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.item.data.GolemHolderMaterial;
import dev.xkmc.modulargolems.content.item.data.GolemUpgrade;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.item.upgrade.IUpgradeItem;
import dev.xkmc.modulargolems.content.recipe.GolemSmithAddSlotRecipe;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.smithing.ISmithingCategoryExtension;
import net.minecraft.resources.Identifier;
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

		Identifier id = null;

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

			ItemStack stack = new ItemStack(golem);
			ArrayList<GolemHolderMaterial.Entry> mats = new ArrayList<>();
			for (var part : golem.getEntityType().values()) {
				GolemPart<?, ?> partItem = part.toItem();
				mats.add(new GolemHolderMaterial.Entry(partItem, id));
			}
			var baseGolem = GolemItems.HOLDER_MAT.set(stack, new GolemHolderMaterial(mats));

			var holder = GolemItems.HOLDER_MAT.set(stack, new GolemHolderMaterial(mats));
			GolemUpgrade.add(holder, item);

			baseSlot.createDisplayOverrides().addItemStacks(List.of(baseGolem));
			outputSlot.createDisplayOverrides().addItemStacks(List.of(holder));
		}

	}

	@Override
	public <T extends IIngredientAcceptor<T>> void setTemplate(GolemSmithAddSlotRecipe r, T t) {
		t.addItemStacks(r.templateIngredient().map(ContextHelper::resolve).orElse(List.of()));
	}

	@Override
	public <T extends IIngredientAcceptor<T>> void setBase(GolemSmithAddSlotRecipe r, T t) {
		t.addItemStacks(ContextHelper.resolve(r.baseIngredient()));
	}

	@Override
	public <T extends IIngredientAcceptor<T>> void setAddition(GolemSmithAddSlotRecipe r, T t) {
		t.addItemStacks(r.additionIngredient().map(ContextHelper::resolve).orElse(List.of()));
	}

}
