package dev.xkmc.modulargolems.content.entity.metalgolem;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 取消肩鞍乘客的独立渲染，避免它被画两次。
 * <p>
 * 判定必须与 {@link MetalSaddleRiderLayer} 严格成对：层里嵌套渲染乘客期间 {@link MetalSaddleRiderLayer#IN_LAYER}
 * 为 true，此时放行；否则取消。之前用"乘客 UUID 加入集合 / 移除"的写法，在多人同屏、多只坐骑同帧渲染、
 * 或乘客自己也被高亮时会出现状态残留 → 某帧的嵌套渲染被误取消，于是乘客只剩独立渲染那一份，
 * 表现就是"中间站着的傀儡偶尔多出一层坐姿渲染"。
 */
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MetalSaddleRiderRenderBlocker {

	@SubscribeEvent
	public static void onPreRender(RenderLivingEvent.Pre<?, ?> event) {
		if (MetalSaddleRiderLayer.IN_LAYER) return;
		var mc = Minecraft.getInstance();
		if (!(event.getEntity().getVehicle() instanceof MetalGolemEntity golem)) return;
		if (!golem.hasGolemSaddle()) return;
		boolean firstPerson = event.getEntity() == mc.cameraEntity && mc.options.getCameraType().isFirstPerson();
		if (!firstPerson) event.setCanceled(true);
	}

}
