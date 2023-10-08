package dev.xkmc.modulargolems.content.entity.humanoid;

import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ItemWrapper {

	ItemWrapper EMPTY = simple(() -> ItemStack.EMPTY, e -> {
	});

	static ItemWrapper simple(Supplier<ItemStack> getter, Consumer<ItemStack> setter) {
		return new Simple(getter, setter);
	}

	ItemStack getItem();

	void setItem(ItemStack stack);

	record Simple(Supplier<ItemStack> getter, Consumer<ItemStack> setter) implements ItemWrapper {
		@Override
		public ItemStack getItem() {
			return getter.get();
		}

		@Override
		public void setItem(ItemStack stack) {
			setter.accept(stack);
		}
	}

}
