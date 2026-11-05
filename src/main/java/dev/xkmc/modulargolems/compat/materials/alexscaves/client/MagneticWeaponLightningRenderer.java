package dev.xkmc.modulargolems.compat.materials.alexscaves.client;

import com.github.alexmodguy.alexscaves.server.entity.item.MagneticWeaponEntity;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexthe666.citadel.client.render.LightningBoltData;
import com.github.alexthe666.citadel.client.render.LightningRender;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 方铅护手的磁力武器：傀儡 controller 的"施法者 → 武器"闪电链。
 * <p>
 * 原版这段画在 {@code MagneticWeaponRenderer.render()} 里，但整段被包在
 * {@code if (controller instanceof Player player)} 中，傀儡的武器只剩一个光秃秃的物品模型。
 * 对那个渲染器做 mixin 注入不生效（泛型覆写 + 桥接方法，refmap 能生成但运行时命不中），
 * 所以干脆在 {@code RenderLevelStageEvent} 里自己遍历实体重画一遍，用同一套 Citadel 闪电。
 */
public class MagneticWeaponLightningRenderer {

	private static final Map<UUID, LightningRender> RENDERS = new HashMap<>();

	public static void register() {
		MinecraftForge.EVENT_BUS.addListener(MagneticWeaponLightningRenderer::onStage);
	}

	private static void onStage(RenderLevelStageEvent event) {
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) return;
		Vec3 cam = event.getCamera().getPosition();
		PoseStack poseStack = event.getPoseStack();
		MultiBufferSource source = mc.renderBuffers().bufferSource();
		float partial = event.getPartialTick();
		for (Entity entity : mc.level.entitiesForRendering()) {
			if (!(entity instanceof MagneticWeaponEntity weapon)) continue;
			Entity controller = weapon.getController();
			if (!(controller instanceof LivingEntity living) || controller instanceof Player) continue;
			if (weapon.getItemStack().isEmpty()) continue;

			// 与 MagneticWeaponEntity#getControllerHandPos 相同的公式（那个方法只接受 Player）
			float yBodyRot = Mth.lerp(partial, living.yBodyRotO, living.yBodyRot);
			boolean mainHand = living.getItemInHand(InteractionHand.MAIN_HAND)
					.is(ACItemRegistry.GALENA_GAUNTLET.get());
			// 原版 getControllerHandPos 用的是 0.68；傀儡的手位置比玩家高一些，抬到 0.85 才对齐
			Vec3 offset = new Vec3(living.getBbWidth() * (mainHand ? -0.75F : 0.75F),
					living.getBbHeight() * 0.85F, living.getBbWidth() * -0.1F)
					.yRot((float) Math.toRadians(-yBodyRot));
			Vec3 from = living.getPosition(partial).add(offset)
					.add(living.getViewVector(partial).normalize().scale(0.75F));
			Vec3 to = weapon.position().add(0, 0.24F, 0);

			int segCount = Mth.clamp((int) weapon.distanceTo(living) + 2, 3, 30);
			float spreadFactor = Mth.clamp((10 - weapon.distanceTo(living)) / 10F * 0.2F, 0.01F, 0.2F);
			float returnProgress = weapon.getReturnProgress(partial);
			LightningBoltData.BoltRenderInfo info = new LightningBoltData.BoltRenderInfo(0.0F,
					spreadFactor, 0.0F, 0.0F,
					new Vector4f(0.1F + returnProgress * 0.9F, 0.1F, 0.9F - returnProgress * 0.8F, 0.8F),
					0.1F);
			LightningBoltData bolt = new LightningBoltData(info, from, to, segCount)
					.size(0.1F)
					.lifespan(1)
					.spawn(LightningBoltData.SpawnFunction.CONSECUTIVE)
					.fade(LightningBoltData.FadeFunction.NONE);

			LightningRender render = RENDERS.computeIfAbsent(weapon.getUUID(), k -> new LightningRender());
			poseStack.pushPose();
			poseStack.translate(-cam.x, -cam.y, -cam.z);
			render.update(weapon, bolt, partial);
			render.render(partial, poseStack, source);
			poseStack.popPose();
		}
	}

}
