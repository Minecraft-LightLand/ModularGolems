package dev.xkmc.modulargolems.compat.materials.alexscaves.client;

import com.github.alexmodguy.alexscaves.client.render.item.RaygunRenderHelper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * 傀儡手持射线枪时的光束，<b>只在第一人称补画</b>。
 * <p>
 * AC 自己的 {@code ClientEvents#postRenderLiving} 有一句 {@code if (!isFirstPerson())}，
 * 第一人称时任何其它实体的光束都不画，因此玩家第一人称看不到傀儡的激光。
 * 第三人称 AC 会正常渲染（见 {@code postRenderLiving} 里的 {@code renderRaysFor}），
 * 这里只补第一人称那一段，不重复。
 * <p>
 * 直接复用 AC 的 {@link RaygunRenderHelper#renderRaysFor}（{@code firstPerson=false} 的第三人称分支），
 * 借它算出正确的枪口位置与发光后处理；因为挂在 {@link RenderLivingEvent.Post} 里，
 * 与 AC 第三人称走的是同一渲染阶段，irradiate 后处理能正常生效（之前自绘在
 * {@code RenderLevelStageEvent} 里调，后处理时机不对所以不发光）。
 */
public class RaygunBeamRenderer {

	public static void register() {
		MinecraftForge.EVENT_BUS.addListener(RaygunBeamRenderer::onPostLiving);
	}

	private static void onPostLiving(RenderLivingEvent.Post<?, ?> event) {
		if (!Minecraft.getInstance().options.getCameraType().isFirstPerson()) return;
		LivingEntity entity = event.getEntity();
		if (!(entity instanceof AbstractGolemEntity<?, ?> golem)) return;
		// 内部会自行判断手持射线枪 + isUsingItem + UseTime >= 5，不满足直接返回
		RaygunRenderHelper.renderRaysFor(golem, golem.getPosition(event.getPartialTick()),
				event.getPoseStack(), event.getMultiBufferSource(), event.getPartialTick(), false, 0);
	}

}
