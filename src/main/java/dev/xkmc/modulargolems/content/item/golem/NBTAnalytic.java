package dev.xkmc.modulargolems.content.item.golem;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class NBTAnalytic {

	public static void analyze(ItemStack stack, List<Component> list) {
		var root = stack.getTag();
		if (root == null) return;

		list.add(log(root, "NBT"));
		if (root.contains(GolemHolder.KEY_ENTITY)) {
			var entity = root.getCompound(GolemHolder.KEY_ENTITY);
			list.add(log(entity, "entity NBT"));
			list.add(log(entity, "equipment", "ArmorItems", "HandItems"));
			list.add(log(entity, "golem data", "auto-serial"));
			list.add(log(entity, "Capability", "ForgeCaps"));
			list.add(log(entity, "Attribute", "Attributes"));
		}
	}

	private static Component log(CompoundTag root, String name, String... key) {
		int ans = 0;
		if (key.length == 0) {
			ans = root.sizeInBytes();
		} else {
			for (String str : key) {
				var tag = root.get(str);
				if (tag == null) continue;
				ans += tag.sizeInBytes();
			}
		}
		Component size = ans >= 1024 ? Component.literal((ans >> 10) + "").withStyle(ChatFormatting.AQUA).append(" kiB") :
				Component.literal(ans + "").withStyle(ChatFormatting.AQUA).append(" bytes");
		return Component.literal(name + " size: ").append(size).withStyle(ChatFormatting.GRAY);
	}

}
