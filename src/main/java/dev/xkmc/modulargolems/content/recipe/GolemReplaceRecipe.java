package dev.xkmc.modulargolems.content.recipe;

import dev.xkmc.l2core.serial.recipe.AbstractShapedRecipe;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.item.data.GolemHolderMaterial;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.item.upgrade.AddSlotTemplate;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import dev.xkmc.modulargolems.init.registrate.GolemMiscs;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNullableByDefault;
import java.util.ArrayList;

public class GolemReplaceRecipe extends AbstractShapedRecipe<GolemReplaceRecipe> {

	public GolemReplaceRecipe(String group, ShapedRecipePattern pattern, ItemStack result) {
		super(group, pattern, result);
	}

	@Override
	public boolean matches(CraftingInput cont, Level level) {
		if (!super.matches(cont, level)) return false;
		for (int i = 0; i < cont.size(); i++) {
			ItemStack input = cont.getItem(i);
			if (!input.isEmpty() && input.getItem() instanceof GolemPart<?, ?>) {
				if (GolemPart.getMaterial(input).isEmpty()) {
					return false;
				}
			}
		}
		var stack = assemble(cont, level.registryAccess());
		if (stack.getItem() instanceof GolemHolder<?, ?> holder) {
			var mats = GolemHolder.getMaterial(stack);
			var upgrades = GolemHolder.getUpgrades(stack);
			int remain = holder.getRemaining(mats, upgrades);
			return remain >= 0;
		}
		return false;
	}

	@Override
	public ItemStack assemble(CraftingInput cont, HolderLookup.Provider access) {
		boolean holderFirst = false;
		ItemStack holder = null;
		ResourceLocation mat = null;
		IGolemPart<?>[] parts = null;
		IGolemPart<?> sel = null;
		for (int i = 0; i < cont.size(); i++) {
			ItemStack input = cont.getItem(i);
			if (input.isEmpty()) continue;
			if (input.getItem() instanceof GolemHolder<?, ?> h) {
				holder = input;
				parts = h.getEntityType().values();
				if (sel == null) holderFirst = true;
			}
			if (input.getItem() instanceof GolemPart<?, ?> p) {
				mat = GolemPart.getMaterial(input).orElse(null);
				sel = p.getPart();
			}
		}
		return replacePart(holder, mat, parts, sel, holderFirst);
	}

	public ItemStack assembleForJEI(ResourceLocation mat) {
		boolean holderFirst = false;
		ItemStack holder = null;
		IGolemPart<?>[] parts = null;
		IGolemPart<?> sel = null;
		for (var ing : getIngredients()) {
			if (ing.isEmpty() || ing.getItems().length != 1)
				continue;
			ItemStack input = ing.getItems()[0];
			if (input.isEmpty()) continue;
			if (input.getItem() instanceof GolemHolder<?, ?> h) {
				holder = input;
				parts = h.getEntityType().values();
				if (sel == null) holderFirst = true;
			}
			if (input.getItem() instanceof GolemPart<?, ?> p) {
				sel = p.getPart();
			}
		}
		return replacePart(holder, mat, parts, sel, holderFirst);
	}

	@ParametersAreNullableByDefault
	public ItemStack replacePart(
			ItemStack holder, ResourceLocation mat,
			IGolemPart<?>[] parts, IGolemPart<?> sel, boolean holderFirst
	) {
		if (holder == null || parts == null || mat == null || sel == null)
			return ItemStack.EMPTY;
		int index = -1;
		if (!holderFirst) {
			for (int i = 0; i < parts.length; i++) {
				if (parts[i].toItem() == sel.toItem()) {
					index = i;
					break;
				}
			}
		} else {
			for (int i = parts.length - 1; i >= 0; i--) {
				if (parts[i].toItem() == sel.toItem()) {
					index = i;
					break;
				}
			}
		}
		if (index < 0)
			return ItemStack.EMPTY;
		if (holder.getItem() instanceof GolemHolder<?, ?> h && h.getEntityType().getBodyPart() == sel) {
			for (var e : GolemHolder.getUpgrades(holder).upgrades()) {
				if (e instanceof AddSlotTemplate) {
					return ItemStack.EMPTY;
				}
			}
		}ItemStack result = holder.copy();
		var matData = GolemItems.HOLDER_MAT.get(result);
		ArrayList<GolemHolderMaterial.Entry> list;
		if (matData == null || matData.size() < parts.length) {
			result = holder.getItem().getDefaultInstance();
			list = new ArrayList<>();
			for (var e : parts) {
				list.add(new GolemHolderMaterial.Entry(e.toItem(), ModularGolems.loc("empty")));
			}
		} else list = matData.copyParts();
		list.set(index, new GolemHolderMaterial.Entry(sel.toItem(), mat));
		return GolemItems.HOLDER_MAT.set(result, new GolemHolderMaterial(list));
	}

	@Override
	public Serializer<GolemReplaceRecipe> getSerializer() {
		return GolemMiscs.REPLACE.get();
	}

}
