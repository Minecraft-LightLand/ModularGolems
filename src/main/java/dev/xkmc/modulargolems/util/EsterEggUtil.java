package dev.xkmc.modulargolems.util;

import dev.xkmc.modulargolems.content.client.override.ModelOverride;
import dev.xkmc.modulargolems.content.client.override.ModelOverrides;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemRenderState;
import dev.xkmc.modulargolems.content.entity.render.AbstractGolemRenderState;
import dev.xkmc.modulargolems.content.item.golem.GolemFacade;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.ModList;

import java.util.Locale;

public class EsterEggUtil {

	public static void registerEsterEggTextures() {

		ModelOverrides.registerOverride(ModularGolems.loc("netherite"), ModelOverride.texturePredicate(
				e -> isKobe(e) ? "_kobe" : ""
		));

	}

	private static boolean isKobe(AbstractGolemRenderState<?, ?, ?> e) {
		if (!hasFacadeWithMaterial(e, ModularGolems.loc("netherite"))) {
			return false;
		}
		String id = e.common().name().getString().toLowerCase(Locale.ROOT);
		return id.equals("kobe") || id.equals("kobe bryant") || id.equals("manba") || id.equals("科比") || id.equals("科比·布莱恩特") || id.equals("曼巴") || id.equals("牢大");
	}

	private static boolean hasFacadeWithMaterial(AbstractGolemRenderState<?, ?, ?> e, Identifier material) {
		if (ModList.get().isLoaded("curios")) {
			var opt = e.common().skin();
			if (opt.getItem() instanceof GolemFacade) {
				return GolemFacade.getMaterial(opt).equals(material);
			}
		}
		if (e instanceof MetalGolemRenderState) {
			for (var tex : e.common().materials()) {
				if (!tex.id().equals(material)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

}
