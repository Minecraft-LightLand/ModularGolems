package dev.xkmc.modulargolems.compat.jei;

import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.content.menu.filter.ItemConfigScreen;
import dev.xkmc.modulargolems.content.menu.ghost.ItemTarget;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemFilterHandler implements IGhostIngredientHandler<ItemConfigScreen> {

	@Override
	public <I> List<Target<I>> getTargetsTyped(ItemConfigScreen gui, ITypedIngredient<I> ingredient, boolean doStart) {
		return ingredient.getIngredient() instanceof ItemStack ? Wrappers.cast(gui.getTargets().stream().map(RTarget::new).toList()) : List.of();
	}

	@Override
	public void onComplete() {

	}

	public record RTarget(ItemTarget target) implements Target<ItemStack> {

		@Override
		public Rect2i getArea() {
			return new Rect2i(target.x(), target.y(), target.w(), target.h());
		}

		@Override
		public void accept(ItemStack ingredient) {
			target.con().accept(ingredient);
		}
	}

}
