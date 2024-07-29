package dev.xkmc.modulargolems.content.item.data;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public record GolemIcon(ArrayList<ItemStack> list) {

	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() != GolemIcon.class) return false;
		return hashCode() == obj.hashCode(); //Not really needed as it's display only
	}

}
