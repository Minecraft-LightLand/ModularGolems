package dev.xkmc.modulargolems.util;

import dev.xkmc.modulargolems.content.client.override.ModelOverride;
import dev.xkmc.modulargolems.content.client.override.ModelOverrides;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.init.ModularGolems;

import java.util.Locale;

public class EsterEggUtil {

	public static void registerEsterEggTextures() {

		ModelOverrides.registerOverride(ModularGolems.loc("kobe"), ModelOverride.texturePredicate(
				e -> isKobe(e) ? "_kobe" : ""
		));

	}

	private static boolean isKobe(AbstractGolemEntity<?, ?> e) {
		if (!(e instanceof MetalGolemEntity)) return false;
		for (var tex : e.getMaterials()) {
			if (!tex.id().equals(ModularGolems.loc("netherite"))) {
				return false;
			}
		}
		String id = e.getDisplayName().getString().toLowerCase(Locale.ROOT);
		return id.equals("kobe") || id.equals("kobe bryant") || id.equals("manba");
	}

}
