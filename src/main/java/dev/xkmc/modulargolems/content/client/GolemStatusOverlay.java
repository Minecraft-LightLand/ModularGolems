package dev.xkmc.modulargolems.content.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.WandItem;
import dev.xkmc.modulargolems.init.data.LangData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.phys.EntityHitResult;
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
		if (golem.getMode() == 0) {
			text.add(LangData.MODE_FOLLOWING.get());
		} else {
			var p = golem.getGuardPos();
			text.add(LangData.MODE_GUARDING.get(p.getX(), p.getY(), p.getZ()));
		}
		golem.getModifiers().forEach((k, v) -> text.add(k.getTooltip(v)));
		renderLongText(gui, screenWidth, screenHeight, poseStack, text);
	}

	private static void renderLongText(ForgeGui gui, int screenWidth, int screenHeight, PoseStack stack, List<Component> list) {
		Font font = gui.getFont();
		int tooltipTextWidth = list.stream().mapToInt(font::width).max().orElse(0);
		int maxWidth = screenWidth / 4;
		List<FormattedCharSequence> ans = list.stream().flatMap(text -> font.split(text, maxWidth).stream()).toList();
		int h = ans.size() * 12;
		int x0 = Math.round(screenWidth / 8f);
		int y0 = Math.round((screenHeight - h) / 2f);

		int y1 = y0;
		int w = Math.min(tooltipTextWidth, maxWidth);

		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tesselator.getBuilder();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		Matrix4f matrix4f = stack.last().pose();
		int bg = 0xf0100010;
		int bs = 0x505000FF;
		int be = 0x5028007f;
		fillGradient(matrix4f, bufferbuilder, x0 - 3, y1 - 4, x0 + w + 3, y1 - 3, 400, bg, bg);
		fillGradient(matrix4f, bufferbuilder, x0 - 3, y1 + h + 3, x0 + w + 3, y1 + h + 4, 400, bg, bg);
		fillGradient(matrix4f, bufferbuilder, x0 - 3, y1 - 3, x0 + w + 3, y1 + h + 3, 400, bg, bg);
		fillGradient(matrix4f, bufferbuilder, x0 - 4, y1 - 3, x0 - 3, y1 + h + 3, 400, bg, bg);
		fillGradient(matrix4f, bufferbuilder, x0 + w + 3, y1 - 3, x0 + w + 4, y1 + h + 3, 400, bg, bg);
		fillGradient(matrix4f, bufferbuilder, x0 - 3, y1 - 3 + 1, x0 - 3 + 1, y1 + h + 3 - 1, 400, bs, be);
		fillGradient(matrix4f, bufferbuilder, x0 + w + 2, y1 - 3 + 1, x0 + w + 3, y1 + h + 3 - 1, 400, bs, be);
		fillGradient(matrix4f, bufferbuilder, x0 - 3, y1 - 3, x0 + w + 3, y1 - 3 + 1, 400, bs, bs);
		fillGradient(matrix4f, bufferbuilder, x0 - 3, y1 + h + 2, x0 + w + 3, y1 + h + 3, 400, be, be);

		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		BufferUploader.drawWithShader(bufferbuilder.end());
		RenderSystem.enableTexture();

		for (FormattedCharSequence text : ans) {
			font.draw(stack, text, x0, y0, 0xFFFFFFFF);
			y0 += 12;
		}
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
	}

}
