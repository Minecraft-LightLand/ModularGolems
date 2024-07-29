package dev.xkmc.modulargolems.content.recipe;

import dev.xkmc.l2core.serial.recipe.AbstractShapedRecipe;
import dev.xkmc.modulargolems.content.item.data.GolemHolderMaterial;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import dev.xkmc.modulargolems.init.registrate.GolemMiscs;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

public class GolemAssembleRecipe extends AbstractShapedRecipe<GolemAssembleRecipe> {

	public GolemAssembleRecipe(String group, ShapedRecipePattern pattern, ItemStack result) {
		super(group, pattern, result);
	}

	@Override
	public boolean matches(CraftingInput cont, Level level) {
		if (!super.matches(cont, level)) return false;
		for (int i = 0; i < cont.size(); i++) {
			ItemStack input = cont.getItem(i);
			if (!input.isEmpty() && input.getItem() instanceof GolemPart part) {
				if (GolemPart.getMaterial(input).isEmpty()) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public ItemStack assemble(CraftingInput cont, HolderLookup.Provider access) {
		ItemStack stack = super.assemble(cont, access);
		ArrayList<GolemHolderMaterial.Entry> list = new ArrayList<>();
		for (int i = 0; i < cont.size(); i++) {
			ItemStack input = cont.getItem(i);
			if (!input.isEmpty() && input.getItem() instanceof GolemPart<?, ?> part) {
				GolemPart.getMaterial(input).ifPresent(mat -> list.add(new GolemHolderMaterial.Entry(part, mat)));
			}
		}
		return GolemItems.HOLDER_MAT.set(stack, new GolemHolderMaterial(list));
	}

	@Override
	public Serializer<GolemAssembleRecipe> getSerializer() {
		return GolemMiscs.ASSEMBLE.get();
	}

}
