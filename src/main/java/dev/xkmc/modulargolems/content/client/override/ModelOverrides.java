package dev.xkmc.modulargolems.content.client.override;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class ModelOverrides {

	private static final HashMap<ResourceLocation, ModelOverride> OVERRIDES = new HashMap<>();

	public static synchronized void registerOverride(ResourceLocation id, ModelOverride override) {
		OVERRIDES.put(id, override);
	}

	public static ModelOverride getOverride(ResourceLocation id) {
		return OVERRIDES.getOrDefault(id, ModelOverride.DEFAULT);
	}

}
