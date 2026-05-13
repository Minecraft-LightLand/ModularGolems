package dev.xkmc.modulargolems.content.recipe;

import dev.xkmc.l2core.serial.recipe.AbstractShapedRecipe;
import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.content.item.data.GolemHolderMaterial;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import dev.xkmc.modulargolems.init.registrate.GolemMiscs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

public class GolemAssembleRecipe extends AbstractShapedRecipe<GolemAssembleRecipe> {

	private final ItemStackTemplate result;

	public GolemAssembleRecipe(CommonInfo commonInfo, CraftingBookInfo bookInfo, ShapedRecipePattern pattern, ItemStackTemplate result) {
		super(commonInfo, bookInfo, pattern, result);
		this.result = result;
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

	public ItemStack getResult() {
		return result.create();
	}

	@Override
	public ItemStack assemble(CraftingInput cont) {
		ItemStack stack = super.assemble(cont);
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
	public RecipeSerializer<ShapedRecipe> getSerializer() {
		return Wrappers.cast(GolemMiscs.ASSEMBLE.get());
	}

}
