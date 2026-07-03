package dev.xkmc.modulargolems.content.recipe;

import dev.xkmc.l2library.serial.recipe.AbstractSmithingRecipe;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.upgrade.IUpgradeItem;
import dev.xkmc.modulargolems.init.registrate.GolemMiscs;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

public class GolemSmithAddSlotRecipe extends AbstractSmithingRecipe<GolemSmithAddSlotRecipe> {

	public final Ingredient template, base, addition;

	public GolemSmithAddSlotRecipe(ResourceLocation id, Ingredient template, Ingredient base, Ingredient addition, ItemStack result) {
		super(id, template, base, addition, result);
		this.template = template;
		this.base = base;
		this.addition = addition;
	}

	@Override
	public boolean matches(Container input, Level level) {
		ItemStack holder = input.getItem(1);
		if (!template.test(input.getItem(0))) return false;
		if (!base.test(holder)) return false;
		var ing = GolemHolder.getCraftMaterial(holder);
		if (!ing.test(input.getItem(2))) return false;
		var upgrade = GolemHolder.getUpgrades(holder);
		if (!(input.getItem(0).getItem() instanceof IUpgradeItem up)) return false;
		if (upgrade.contains(up)) return false;
		var mat = GolemHolder.getMaterial(holder);
		upgrade = new ArrayList<>(upgrade);
		upgrade.add(up);
		return holder.getItem() instanceof GolemHolder<?, ?> item &&
				item.getRemaining(mat, upgrade) >= 0;
	}

	@Override
	public ItemStack assemble(Container input, RegistryAccess pvd) {
		ItemStack stack = input.getItem(1).copy();
		GolemHolder.addUpgrade(stack, (IUpgradeItem) input.getItem(0).getItem());
		return stack;
	}

	@Override
	public boolean isAdditionIngredient(ItemStack stack) {
		for (var e : GolemMaterialConfig.get().ingredients.values()) {
			if (e.test(stack))
				return true;
		}
		return false;
	}

	@Override
	// 序列化
	public Serializer<GolemSmithAddSlotRecipe> getSerializer() {
		return GolemMiscs.SMITH_ADD_SLOT.get();
	}

}
