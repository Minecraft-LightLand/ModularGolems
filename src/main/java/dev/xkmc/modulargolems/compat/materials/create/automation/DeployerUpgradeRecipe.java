package dev.xkmc.modulargolems.compat.materials.create.automation;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record DeployerUpgradeRecipe(ItemStack result) implements Recipe<RecipeInput> {

	@Override
	public ItemStack assemble(RecipeInput p_345149_, HolderLookup.Provider p_346030_) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return result.copy();
	}

	@Override
	public boolean matches(RecipeInput container, Level level) {
		return false;
	}

	@Override
	public boolean canCraftInDimensions(int w, int h) {
		return false;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return null;
	}

	@Override
	public RecipeType<?> getType() {
		return null;
	}

}
