package dev.xkmc.modulargolems.content.item.data;

import dev.xkmc.modulargolems.content.item.upgrade.UpgradeItem;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public record GolemUpgrade(int extraSlot, ArrayList<Item> upgrades) {

	public static ItemStack add(ItemStack stack, UpgradeItem item) {
		var data = GolemItems.UPGRADE.get(stack);
		if (data == null) data = new GolemUpgrade(0, new ArrayList<>());
		var list = new ArrayList<>(data.upgrades);
		list.add(item);
		return GolemItems.UPGRADE.set(stack, new GolemUpgrade(data.extraSlot, list));
	}

	public static void removeAll(ItemStack stack) {
		var data = GolemItems.UPGRADE.get(stack);
		if (data == null) data = new GolemUpgrade(0, new ArrayList<>());
		GolemItems.UPGRADE.set(stack, new GolemUpgrade(data.extraSlot, new ArrayList<>()));
	}


}
