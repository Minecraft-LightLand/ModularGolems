package dev.xkmc.modulargolems.compat.jei;

import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.upgrade.IUpgradeItem;
import dev.xkmc.modulargolems.content.recipe.GolemSmithAddSlotRecipe;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.recipe.category.extensions.vanilla.smithing.ISmithingCategoryExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record GolemAddSlotExtension(
) implements ISmithingCategoryExtension<GolemSmithAddSlotRecipe> {

	@Override
	public <T extends IIngredientAcceptor<T>> void setTemplate(GolemSmithAddSlotRecipe r, T t) {
		t.addIngredients(r.template);
	}

	@Override
	public <T extends IIngredientAcceptor<T>> void setBase(GolemSmithAddSlotRecipe r, T t) {
		if (r.base.getItems()[0].getItem() instanceof GolemHolder<?, ?> golem) {
			List<ItemStack> list = new ArrayList<>();
			for (ResourceLocation rl : GolemMaterialConfig.get().getAllMaterials()) {
				ItemStack stack = new ItemStack(golem);
				for (var part : golem.getEntityType().values()) {
					GolemHolder.addMaterial(stack, part.toItem(), rl);
				}
				var mat = GolemMaterialConfig.get().ingredients.get(rl);
				for (var ing : mat.getItems()) {
					list.add(stack);
				}
			}
			t.addItemStacks(list);
		} else t.addIngredients(r.base);
	}

	@Override
	public <T extends IIngredientAcceptor<T>> void setAddition(GolemSmithAddSlotRecipe r, T t) {
		if (r.base.getItems()[0].getItem() instanceof GolemHolder<?, ?>) {
			List<ItemStack> list = new ArrayList<>();
			for (ResourceLocation rl : GolemMaterialConfig.get().getAllMaterials()) {
				var mat = GolemMaterialConfig.get().ingredients.get(rl);
				list.addAll(List.of(mat.getItems()));
			}
			t.addItemStacks(list);
		} else t.addIngredients(r.addition);
	}

	@Override
	public <T extends IIngredientAcceptor<T>> void setOutput(GolemSmithAddSlotRecipe r, T t) {
		if (r.template.getItems()[0].getItem() instanceof IUpgradeItem item &&
				r.base.getItems()[0].getItem() instanceof GolemHolder<?, ?> golem) {
			List<ItemStack> list = new ArrayList<>();
			for (ResourceLocation rl : GolemMaterialConfig.get().getAllMaterials()) {
				ItemStack stack = new ItemStack(golem);
				for (var part : golem.getEntityType().values()) {
					GolemHolder.addMaterial(stack, part.toItem(), rl);
				}
				var mat = GolemMaterialConfig.get().ingredients.get(rl);
				GolemHolder.addUpgrade(stack, item);
				for (var ing : mat.getItems()) {
					list.add(stack);
				}
			}
			t.addItemStacks(list);
		}
	}
}
