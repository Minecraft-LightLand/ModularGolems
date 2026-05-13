package dev.xkmc.modulargolems.content.item.wand;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.l2itemselector.select.item.CustomDisplaySelectItem;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class BaseWandItem extends Item implements CustomDisplaySelectItem {

	@Nullable
	private final ItemEntry<? extends BaseWandItem> base;
	@Nullable
	private final MGLangData right, shift;

	public BaseWandItem(Properties properties, @Nullable MGLangData right, @Nullable MGLangData shift, @Nullable ItemEntry<? extends BaseWandItem> base) {
		super(properties);
		this.base = base;
		this.right = right;
		this.shift = shift;

	}

	@Override
	public ItemStack getDisplay(Identifier id, ItemStack stack) {
		return base == null ? stack : base.asStack();
	}

	@Override
	public final void appendHoverText(ItemStack stack, TooltipContext level, TooltipDisplay disp, Consumer<Component> list, TooltipFlag flag) {
		if (base != null) {
			list.accept(MGLangData.WAND_MODE.get(base.asStack().getHoverName()));
			list.accept(MGLangData.WAND_SWITCH.get());
		}
		if (right != null) {
			list.accept(MGLangData.WAND_RIGHT.get());
			list.accept(right.get());
		}
		if (shift != null) {
			list.accept(MGLangData.WAND_SHIFT.get());
			list.accept(shift.get());
		}
	}
}
