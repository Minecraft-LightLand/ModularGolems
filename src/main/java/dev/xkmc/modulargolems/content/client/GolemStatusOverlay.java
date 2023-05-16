package dev.xkmc.modulargolems.content.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import dev.xkmc.l2library.base.overlay.OverlayUtils;
import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.item.WandItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.ArrayList;
import java.util.List;

public class GolemStatusOverlay extends GuiComponent implements IGuiOverlay {

	@Override
	public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
		LocalPlayer player = Proxy.getClientPlayer();
		if (player == null) return;
		if (!(player.getMainHandItem().getItem() instanceof WandItem)) return;
		var hit = Minecraft.getInstance().hitResult;
		if (!(hit instanceof EntityHitResult entityHit)) return;
		if (!(entityHit.getEntity() instanceof AbstractGolemEntity<?, ?> golem)) return;
		gui.setupOverlayRenderState(true, false);
		List<Component> text = new ArrayList<>();
		text.add(golem.getName());
		text.add(golem.getMode().getDesc(golem));
		golem.getModifiers().forEach((k, v) -> text.add(k.getTooltip(v)));
		OverlayUtils util = new OverlayUtils(screenWidth, screenHeight);
		util.renderLongText(gui, poseStack, text);
		if (!(golem instanceof HumanoidGolemEntity humanoid)) return;
		util.bg = 0xffc6c6c6;
		List<ClientTooltipComponent> list = List.of(new GolemEquipmentTooltip(humanoid));
		renderTooltipInternal(util, poseStack, screenWidth, screenHeight, list);
	}

	private static void renderTooltipInternal(OverlayUtils utils, PoseStack poseStack, int sw, int sh, List<ClientTooltipComponent> list) {
		if (list.isEmpty()) return;
		Font font = Minecraft.getInstance().font;
		ItemRenderer ir = Minecraft.getInstance().getItemRenderer();
		int w = 0;
		int h = list.size() == 1 ? -2 : 0;
		for (ClientTooltipComponent ctc : list) {
			int k = ctc.getWidth(font);
			if (k > w) {
				w = k;
			}
			h += ctc.getHeight();
		}

		int x = (int)(sw * 0.7);
		int y = Math.round((float) (sh - h) / 2.0F);
		if (x + w > sw) {
			x -= 28 + w;
		}

		if (y + h + 6 > sh) {
			y = sh - h - 6;
		}

		poseStack.pushPose();
		float f = ir.blitOffset;
		int z = 400;
		ir.blitOffset = z;
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tesselator.getBuilder();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		Matrix4f matrix4f = poseStack.last().pose();
		fillGradient(matrix4f, bufferbuilder, x - 3, y - 4, x + w + 3, y - 3, z, utils.bg, utils.bg);
		fillGradient(matrix4f, bufferbuilder, x - 3, y + h + 3, x + w + 3, y + h + 4, z, utils.bg, utils.bg);
		fillGradient(matrix4f, bufferbuilder, x - 3, y - 3, x + w + 3, y + h + 3, z, utils.bg, utils.bg);
		fillGradient(matrix4f, bufferbuilder, x - 4, y - 3, x - 3, y + h + 3, z, utils.bg, utils.bg);
		fillGradient(matrix4f, bufferbuilder, x + w + 3, y - 3, x + w + 4, y + h + 3, z, utils.bg, utils.bg);
		fillGradient(matrix4f, bufferbuilder, x - 3, y - 3 + 1, x - 3 + 1, y + h + 3 - 1, z, utils.bs, utils.be);
		fillGradient(matrix4f, bufferbuilder, x + w + 2, y - 3 + 1, x + w + 3, y + h + 3 - 1, z, utils.bs, utils.be);
		fillGradient(matrix4f, bufferbuilder, x - 3, y - 3, x + w + 3, y - 3 + 1, z, utils.bs, utils.bs);
		fillGradient(matrix4f, bufferbuilder, x - 3, y + h + 2, x + w + 3, y + h + 3, z, utils.be, utils.be);
		RenderSystem.enableDepthTest();
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		BufferUploader.drawWithShader(bufferbuilder.end());
		RenderSystem.disableBlend();
		RenderSystem.enableTexture();
		MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
		poseStack.translate(0.0D, 0.0D, z);
		int iy = y;
		for (int i = 0; i < list.size(); ++i) {
			ClientTooltipComponent ctc = list.get(i);
			ctc.renderText(font, x, iy, matrix4f, buffer);
			iy += ctc.getHeight() + (i == 0 ? 2 : 0);
		}
		buffer.endBatch();
		poseStack.popPose();
		iy = y;
		for (int i = 0; i < list.size(); ++i) {
			ClientTooltipComponent ctc = list.get(i);
			ctc.renderImage(font, x, iy, poseStack, ir, 400);
			iy += ctc.getHeight() + (i == 0 ? 2 : 0);
		}
		ir.blitOffset = f;
	}

	private record GolemEquipmentTooltip(HumanoidGolemEntity golem) implements ClientTooltipComponent {

		public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/gui/container/bundle.png");

		@Override
		public int getHeight() {
			return 72;
		}

		@Override
		public int getWidth(Font pFont) {
			return 36;
		}

		@Override
		public void renderImage(Font font, int mx, int my, PoseStack pose, ItemRenderer ir, int offset) {
			renderSlot(font, mx, my + 18, pose, golem.getItemBySlot(EquipmentSlot.MAINHAND), ir, offset);
			renderSlot(font, mx, my + 36, pose, golem.getItemBySlot(EquipmentSlot.OFFHAND), ir, offset);
			renderSlot(font, mx + 18, my, pose, golem.getItemBySlot(EquipmentSlot.HEAD), ir, offset);
			renderSlot(font, mx + 18, my + 18, pose, golem.getItemBySlot(EquipmentSlot.CHEST), ir, offset);
			renderSlot(font, mx + 18, my + 36, pose, golem.getItemBySlot(EquipmentSlot.LEGS), ir, offset);
			renderSlot(font, mx + 18, my + 54, pose, golem.getItemBySlot(EquipmentSlot.FEET), ir, offset);
		}

		private void renderSlot(Font font, int x, int y, PoseStack pose, ItemStack stack, ItemRenderer ir, int offset) {
			this.blit(pose, x, y, offset);
			if (stack.isEmpty()) return;
			ir.renderAndDecorateItem(stack, x + 1, y + 1, 0);
			ir.renderGuiItemDecorations(font, stack, x + 1, y + 1);
		}

		private void blit(PoseStack poseStack, int x, int y, int z) {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
			GuiComponent.blit(poseStack, x, y, z, 0, 0, 18, 18, 128, 128);
		}

	}

}
