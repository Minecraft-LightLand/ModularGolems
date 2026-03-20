package dev.xkmc.modulargolems.util;

import dev.xkmc.modulargolems.compat.curio.CurioCompatRegistry;
import dev.xkmc.modulargolems.content.client.override.ModelOverride;
import dev.xkmc.modulargolems.content.client.override.ModelOverrides;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.GolemFacade;
import dev.xkmc.modulargolems.init.ModularGolems;

import java.util.Locale;
import net.minecraft.resources.ResourceLocation;

public class EsterEggUtil {

	public static void registerEsterEggTextures() {

		ModelOverrides.registerOverride(ModularGolems.loc("netherite"), ModelOverride.texturePredicate(
				e -> isKobe(e) ? "_kobe" : ""
		));

	}

	private static boolean isKobe(AbstractGolemEntity<?, ?> e) {
		if (!hasFacadeWithMaterial(e, ModularGolems.loc("netherite"))) {
			return false;
		}
		String id = e.getDisplayName().getString().toLowerCase(Locale.ROOT);
		return id.equals("kobe") || id.equals("kobe bryant") || id.equals("manba");
	}

	private static boolean hasFacadeWithMaterial(AbstractGolemEntity<?, ?> e, ResourceLocation material) {
		var opt = CurioCompatRegistry.getItem(e, "golem_skin");
		if (opt.isPresent() && opt.get().getItem() instanceof GolemFacade facade) {
			return GolemFacade.getMaterial(opt.get()).equals(material);
		}
		if (e instanceof MetalGolemEntity) {
			for (var tex : e.getMaterials()) {
				if (!tex.id().equals(material)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
}