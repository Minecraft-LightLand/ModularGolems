package dev.xkmc.modulargolems.content.client.weapon;

import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class GolemModelAnimations {

	public static final Map<Identifier, AnimationDefinition> MAP = new HashMap<>();

	public synchronized static void register(Identifier id, AnimationDefinition path) {
		MAP.put(id, path);
	}

}
