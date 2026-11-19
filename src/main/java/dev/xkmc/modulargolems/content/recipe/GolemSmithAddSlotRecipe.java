package dev.xkmc.modulargolems.content.recipe;

import dev.xkmc.l2core.serial.recipe.AbstractSmithingRecipe;
import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.item.data.GolemUpgrade;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.upgrade.IUpgradeItem;
import dev.xkmc.modulargolems.init.registrate.GolemMiscs;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

public class GolemSmithAddSlotRecipe extends AbstractSmithingRecipe<GolemSmithAddSlotRecipe> {

	public final Ingredient template, base, addition;

	public GolemSmithAddSlotRecipe(Ingredient template, Ingredient base, Ingredient addition, ItemStack result) {
		super(template, base, addition, result);
		this.template = template;
		this.base = base;
		this.addition = addition;
	}

	@Override
	public boolean matches(SmithingRecipeInput input, Level level) {
		ItemStack holder = input.base();
		if (!template.test(input.template())) return false;
		if (!base.test(holder)) return false;
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
	public boolean isAdditionIngredient(ItemStack stack) {
		return GolemMaterial.getMaterial(stack).isPresent();
	}

	@Override
	public ItemStack assemble(SmithingRecipeInput input, HolderLookup.Provider pvd) {
		ItemStack stack = input.base().copy();
		GolemUpgrade.add(stack, (IUpgradeItem) input.template().getItem());
		return stack;
	}

	@Override
	public Serializer<GolemSmithAddSlotRecipe> getSerializer() {
		return GolemMiscs.SMITH_ADD_SLOT.get();
	}

}
