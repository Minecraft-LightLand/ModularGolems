package dev.xkmc.modulargolems.content.entity.metalgolem;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** 取消肩鞍乘客的独立渲染，避免它被画两次。 */
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MetalSaddleRiderRenderBlocker {

	@SubscribeEvent
	public static void onPreRender(RenderLivingEvent.Pre<?, ?> event) {
		var uuid = event.getEntity().getUUID();
		if (!MetalSaddleRiderLayer.BLOCKED.contains(uuid)) return;
		var mc = Minecraft.getInstance();
		boolean firstPerson = event.getEntity() == mc.cameraEntity && mc.options.getCameraType().isFirstPerson();
		if (!firstPerson) event.setCanceled(true);
		MetalSaddleRiderLayer.BLOCKED.remove(uuid);
	}

}
