package dev.xkmc.modulargolems.content.item.wand;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.l2itemselector.select.item.CustomDisplaySelectItem;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
	public ItemStack getDisplay(ResourceLocation id, ItemStack stack) {
		return base == null ? stack : base.asStack();
	}

	@Override
	public final void appendHoverText(ItemStack stack, TooltipContext level, List<Component> list, TooltipFlag flag) {
		if (base != null) {
			list.add(MGLangData.WAND_MODE.get(base.asStack().getHoverName()));
			list.add(MGLangData.WAND_SWITCH.get());
		}
		if (right != null) {
			list.add(MGLangData.WAND_RIGHT.get());
			list.add(right.get());
		}
		if (shift != null) {
			list.add(MGLangData.WAND_SHIFT.get());
			list.add(shift.get());
		}
	}
}
