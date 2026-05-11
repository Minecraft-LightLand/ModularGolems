package dev.xkmc.modulargolems.content.recipe;

import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.init.registrate.GolemMiscs;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.LinkedHashSet;
import java.util.stream.Stream;

public record GolemMaterialIngredient() implements ICustomIngredient {

	@Override
	public SlotDisplay display() {
		return new SlotDisplay.ItemSlotDisplay(Items.IRON_INGOT);
	}

	@Override
	public boolean test(ItemStack stack) {
		return false;
	}

	@Override
	public Stream<Holder<Item>> items() {
		var ingredients = GolemMaterialConfig.get().ingredients.values();
		var items = new LinkedHashSet<Holder<Item>>();
		for (var e : ingredients) {
			e.items().forEach(items::add);
		}
		return items.stream();
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public IngredientType<?> getType() {
		return GolemMiscs.ING_MAT.get();
	}

}
