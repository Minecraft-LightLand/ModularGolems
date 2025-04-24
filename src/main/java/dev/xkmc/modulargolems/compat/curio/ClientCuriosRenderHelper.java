package dev.xkmc.modulargolems.compat.curio;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemRenderer;
import top.theillusivec4.curios.client.render.CuriosLayer;

public class ClientCuriosRenderHelper {
	public static void addLayer(HumanoidGolemRenderer r) {
		try {
			r.addLayer(new CuriosLayer<>(r));
		} catch (Exception ignored) {
		}
	}
}
