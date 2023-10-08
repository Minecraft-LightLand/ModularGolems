package dev.xkmc.modulargolems.content.client;

import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.resources.ResourceLocation;

public class GolemModelPaths {

	public static final ResourceLocation
			HELMETS = modLoc("helmet"),
			CHESTPLATES = modLoc("chestplate"),
			LEGGINGS = modLoc("shinguard");

	public static ResourceLocation modLoc(String str) {
		return new ResourceLocation(ModularGolems.MODID, str);
	}

}
