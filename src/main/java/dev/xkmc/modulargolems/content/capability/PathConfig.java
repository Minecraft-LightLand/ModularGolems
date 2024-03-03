package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.modulargolems.content.item.card.PathRecordCard;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@SerialClass
public class PathConfig {

	@SerialClass.SerialField
	protected final ArrayList<ItemStack> path = new ArrayList<>();

	public List<PathRecordCard.Pos> getList() {
		for (var e : path) {
			if (e.getItem() instanceof PathRecordCard) {
				return PathRecordCard.getList(e);
			}
		}
		return List.of();
	}
}
