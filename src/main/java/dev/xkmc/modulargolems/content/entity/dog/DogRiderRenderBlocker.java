package dev.xkmc.modulargolems.content.entity.dog;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 取消 {@link DogRiderLayer#BLOCKED} 里乘客的独立渲染，让它只在犬形已旋转的 poseStack 中被画一次，
 * 从而在激流旋身时跟随坐骑一起翻滚。
 */
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DogRiderRenderBlocker {

	@SubscribeEvent
	public static void onPreRender(RenderLivingEvent.Pre<?, ?> event) {
		var uuid = event.getEntity().getUUID();
		if (!DogRiderLayer.BLOCKED.contains(uuid)) return;
		var mc = Minecraft.getInstance();
		boolean firstPerson = event.getEntity() == mc.cameraEntity && mc.options.getCameraType().isFirstPerson();
		if (!firstPerson) event.setCanceled(true);
		DogRiderLayer.BLOCKED.remove(uuid);
	}

}
