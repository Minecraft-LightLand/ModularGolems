package dev.xkmc.modulargolems.compat.curio;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemRenderer;
import top.theillusivec4.curios.client.render.CuriosLayer;

public class CuriosClientRegistry {

	public static void createLayer(HumanoidGolemRenderer renderer) {
		try {
			renderer.addLayer(new CuriosLayer<>(renderer));
		} catch (Throwable ignored) {
		}
	}

}
