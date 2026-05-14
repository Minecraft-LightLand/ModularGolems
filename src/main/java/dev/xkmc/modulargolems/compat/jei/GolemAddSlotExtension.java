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
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.recipe.category.extensions.vanilla.smithing.ISmithingCategoryExtension;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record GolemAddSlotExtension(
) implements ISmithingCategoryExtension<GolemSmithAddSlotRecipe> {

	@Override
	public <T extends IIngredientAcceptor<T>> void setTemplate(GolemSmithAddSlotRecipe r, T t) {
		t.addItemStacks(r.templateIngredient().map(ContextHelper::resolve).orElse(List.of()));
	}

	@Override
	public <T extends IIngredientAcceptor<T>> void setBase(GolemSmithAddSlotRecipe r, T t) {
		var base = ContextHelper.resolve(r.baseIngredient()).getFirst();
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
				for (var ing : ContextHelper.resolve(mat)) {
					list.add(holder);
				}
			}
			t.addItemStacks(list);
		} else
			t.addItemStacks(r.additionIngredient().map(ContextHelper::resolve).orElse(List.of()));
	}

	@Override
	public <T extends IIngredientAcceptor<T>> void setAddition(GolemSmithAddSlotRecipe r, T t) {
		var base = ContextHelper.resolve(r.baseIngredient()).getFirst();
		if (base.getItem() instanceof GolemHolder<?, ?>) {
			List<ItemStack> list = new ArrayList<>();
			for (Identifier rl : GolemMaterialConfig.get().getAllMaterials()) {
				var mat = GolemMaterialConfig.get().getRepairIngredient(rl);
				if (mat == null) continue;
				list.addAll(ContextHelper.resolve(mat));
			}
			t.addItemStacks(list);
		} else
			t.addItemStacks(r.additionIngredient().map(ContextHelper::resolve).orElse(List.of()));
	}

	@Override
	public <T extends IIngredientAcceptor<T>> void setOutput(GolemSmithAddSlotRecipe r, T t) {
		var template = r.templateIngredient().map(ContextHelper::resolve)
				.filter(e -> !e.isEmpty())
				.map(List::getFirst).orElse(ItemStack.EMPTY);
		var base = ContextHelper.resolve(r.baseIngredient()).getFirst();
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
				for (var ing : ContextHelper.resolve(mat)) {
					list.add(holder);
				}
			}
			t.addItemStacks(list);
		}
	}
}
