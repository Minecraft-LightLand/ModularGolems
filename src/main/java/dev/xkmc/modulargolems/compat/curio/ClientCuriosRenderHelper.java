package dev.xkmc.modulargolems.compat.curio;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import top.theillusivec4.curios.client.CuriosLayer;

public class ClientCuriosRenderHelper {
	public static void addLayer(HumanoidGolemRenderer r, EntityRendererProvider.Context ctx) {
		try {
			r.addLayer(new CuriosLayer<>(r, ctx));
		} catch (Exception ignored) {
		}
	}
}
