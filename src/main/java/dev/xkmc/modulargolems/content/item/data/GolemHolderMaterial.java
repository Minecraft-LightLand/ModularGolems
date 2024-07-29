package dev.xkmc.modulargolems.content.item.data;

import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.ArrayList;

public record GolemHolderMaterial(ArrayList<Entry> parts) {

	public static GolemHolderMaterial parse(ArrayList<GolemMaterial> materials) {
		ArrayList<Entry> list = new ArrayList<>();
		for (var mat : materials) {
			list.add(new Entry(mat.part(), mat.id()));
		}
		return new GolemHolderMaterial(list);
	}

	public ArrayList<GolemMaterial> toList() {
		ArrayList<GolemMaterial> list = new ArrayList<>();
		for (var e : parts) {
			if (e.part instanceof GolemPart<?, ?> part) {
				list.add(part.parseMaterial(e.material()));
			}
		}
		return list;
	}

	public record Entry(Item part, ResourceLocation material) {

	}

}
