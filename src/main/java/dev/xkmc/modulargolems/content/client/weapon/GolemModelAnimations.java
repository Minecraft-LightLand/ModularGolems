package dev.xkmc.modulargolems.content.client.weapon;

import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class GolemModelAnimations {

	public static final Map<ResourceLocation, AnimationDefinition> MAP = new HashMap<>();

	public synchronized static void register(ResourceLocation id, AnimationDefinition path) {
		MAP.put(id, path);
	}

}
