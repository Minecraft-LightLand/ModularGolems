package dev.xkmc.modulargolems.compat.jei;

import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.item.data.GolemHolderMaterial;
import dev.xkmc.modulargolems.content.item.data.GolemUpgrade;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.item.upgrade.IUpgradeItem;
import dev.xkmc.modulargolems.content.recipe.GolemSmithAddSlotRecipe;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.recipe.category.extensions.vanilla.smithing.ISmithingCategoryExtension;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record GolemAddSlotExtension(
) implements ISmithingCategoryExtension<GolemSmithAddSlotRecipe> {

	@Override
	public <T extends IIngredientAcceptor<T>> void setTemplate(GolemSmithAddSlotRecipe r, T t) {
		t.addItemStacks(r.templateIngredient().map(e -> e.display().resolveForStacks(ContextMap.EMPTY)).orElse(List.of()));
	}

	@Override
	public <T extends IIngredientAcceptor<T>> void setBase(GolemSmithAddSlotRecipe r, T t) {
		var base = r.baseIngredient().display().resolveForStacks(ContextMap.EMPTY).getFirst();
		if (base.getItem() instanceof GolemHolder<?, ?> golem) {
			List<ItemStack> list = new ArrayList<>();
			for (Identifier rl : GolemMaterialConfig.get().getAllMaterials()) {
				var mat = GolemMaterialConfig.get().getRepairIngredient(rl);
				if (mat == null) continue;
				ItemStack stack = new ItemStack(golem);
				ArrayList<GolemHolderMaterial.Entry> mats = new ArrayList<>();
				for (var part : golem.getEntityType().values()) {
					GolemPart<?, ?> partItem = part.toItem();
					mats.add(new GolemHolderMaterial.Entry(partItem, rl));
				}
				var holder = GolemItems.HOLDER_MAT.set(stack, new GolemHolderMaterial(mats));
				for (var ing : mat.display().resolveForStacks(ContextMap.EMPTY)) {
					list.add(holder);
				}
			}
			t.addItemStacks(list);
		} else
			t.addItemStacks(r.additionIngredient().map(e -> e.display().resolveForStacks(ContextMap.EMPTY)).orElse(List.of()));
	}

	@Override
	public <T extends IIngredientAcceptor<T>> void setAddition(GolemSmithAddSlotRecipe r, T t) {
		var base = r.baseIngredient().display().resolveForStacks(ContextMap.EMPTY).getFirst();
		if (base.getItem() instanceof GolemHolder<?, ?>) {
			List<ItemStack> list = new ArrayList<>();
			for (Identifier rl : GolemMaterialConfig.get().getAllMaterials()) {
				var mat = GolemMaterialConfig.get().getRepairIngredient(rl);
				if (mat == null) continue;
				list.addAll(mat.display().resolveForStacks(ContextMap.EMPTY));
			}
			t.addItemStacks(list);
		} else
			t.addItemStacks(r.additionIngredient().map(e -> e.display().resolveForStacks(ContextMap.EMPTY)).orElse(List.of()));
	}

	@Override
	public <T extends IIngredientAcceptor<T>> void setOutput(GolemSmithAddSlotRecipe r, T t) {
		var template = r.templateIngredient().map(e -> e.display().resolveForStacks(ContextMap.EMPTY).getFirst()).orElse(ItemStack.EMPTY);
		var base = r.baseIngredient().display().resolveForStacks(ContextMap.EMPTY).getFirst();
		if (template.getItem() instanceof IUpgradeItem item &&
				base.getItem() instanceof GolemHolder<?, ?> golem) {
			List<ItemStack> list = new ArrayList<>();
			for (Identifier rl : GolemMaterialConfig.get().getAllMaterials()) {
				var mat = GolemMaterialConfig.get().getRepairIngredient(rl);
				if (mat == null) continue;
				ItemStack stack = new ItemStack(golem);
				ArrayList<GolemHolderMaterial.Entry> mats = new ArrayList<>();
				for (var part : golem.getEntityType().values()) {
					GolemPart<?, ?> partItem = part.toItem();
					mats.add(new GolemHolderMaterial.Entry(partItem, rl));
				}
				var holder = GolemItems.HOLDER_MAT.set(stack, new GolemHolderMaterial(mats));
				GolemUpgrade.add(holder, item);
				for (var ing : mat.display().resolveForStacks(ContextMap.EMPTY)) {
					list.add(holder);
				}
			}
			t.addItemStacks(list);
		}
	}
}
