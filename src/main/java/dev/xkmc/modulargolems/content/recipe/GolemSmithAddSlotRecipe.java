package dev.xkmc.modulargolems.content.recipe;

import dev.xkmc.l2core.serial.recipe.AbstractSmithingRecipe;
import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.content.item.data.GolemUpgrade;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.upgrade.IUpgradeItem;
import dev.xkmc.modulargolems.init.registrate.GolemMiscs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Optional;

public class GolemSmithAddSlotRecipe extends AbstractSmithingRecipe<GolemSmithAddSlotRecipe> {

	public GolemSmithAddSlotRecipe(CommonInfo commonInfo, Optional<Ingredient> template, Ingredient base, Optional<Ingredient> addition, ItemStackTemplate result) {
		super(commonInfo, template, base, addition, result);
	}

	@Override
	public boolean matches(SmithingRecipeInput input, Level level) {
		ItemStack holder = input.base();
		if (templateIngredient().isPresent() && !templateIngredient().get().test(input.template())) return false;
		if (!baseIngredient().test(holder)) return false;
		var ing = GolemHolder.getCraftingMaterial(holder);
		if (ing == null || !ing.test(input.addition())) return false;
		var upgrade = GolemHolder.getUpgrades(holder);
		if (!(input.template().getItem() instanceof IUpgradeItem up)) return false;
		if (upgrade.contains(up.asItem())) return false;
		var mat = GolemHolder.getMaterial(holder);
		var newUpgrade = new GolemUpgrade(upgrade.extraSlot(), new ArrayList<>(upgrade.upgrades()));
		newUpgrade.upgrades().add(up.asItem());
		return holder.getItem() instanceof GolemHolder<?, ?> item &&
				item.getRemaining(mat, newUpgrade) >= 0;
	}

	@Override
	protected PlacementInfo createPlacementInfo() {
		return super.createPlacementInfo();
	}

	@Override
	public ItemStack assemble(SmithingRecipeInput input) {
		ItemStack stack = input.base().copy();
		GolemUpgrade.add(stack, (IUpgradeItem) input.template().getItem());
		return stack;
	}

	@Override
	public RecipeSerializer<SmithingTransformRecipe> getSerializer() {
		return Wrappers.cast(GolemMiscs.SMITH_ADD_SLOT.get());
	}

}
