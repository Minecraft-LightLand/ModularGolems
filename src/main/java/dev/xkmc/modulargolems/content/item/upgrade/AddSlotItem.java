package dev.xkmc.modulargolems.content.item.upgrade;

import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class AddSlotItem extends Item {

	public final int slot;

	public AddSlotItem(Properties properties, int slot) {
		super(properties);
		this.slot = slot;
	}


	@Override
	public void appendHoverText(ItemStack stack, TooltipContext level, TooltipDisplay disp, Consumer<Component> list, TooltipFlag flag) {
		list.accept(MGLangData.ADD_SLOT.get(slot));
	}

}
