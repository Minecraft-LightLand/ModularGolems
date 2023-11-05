package dev.xkmc.modulargolems.content.menu.target;

import dev.xkmc.modulargolems.content.menu.ghost.GhostItemScreen;
import dev.xkmc.modulargolems.content.menu.registry.ConfigGroup;
import dev.xkmc.modulargolems.content.menu.registry.GolemTabRegistry;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabManager;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;

public class TargetConfigScreen extends GhostItemScreen<TargetConfigMenu> {

	private boolean hoverHostile, hoverFriendly;

	public TargetConfigScreen(TargetConfigMenu cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
	}


	@Override
	protected void init() {
		super.init();
		new GolemTabManager<>(this, new ConfigGroup(menu.editor.editor()))
				.init(this::addRenderableWidget, GolemTabRegistry.CONFIG_TARGET);
	}

	@Override
	protected void renderLabels(GuiGraphics g, int x, int y) {
		super.renderLabels(g, x, y);
		drawLeft(g, MGLangData.UI_TARGET_HOSTILE.get(), 13);
		drawLeft(g, MGLangData.UI_TARGET_FRIENDLY.get(), 62);
		hoverHostile = drawRight(g, MGLangData.UI_TARGET_RESET.get().withStyle(ChatFormatting.UNDERLINE), 13, x, y);
		hoverFriendly = drawRight(g, MGLangData.UI_TARGET_RESET.get().withStyle(ChatFormatting.UNDERLINE), 62, x, y);
	}

	private void drawLeft(GuiGraphics g, Component comp, int y) {
		int x = titleLabelX;
		y += titleLabelY;
		g.drawString(font, comp, x, y, 4210752, false);
	}

	private boolean drawRight(GuiGraphics g, MutableComponent comp, int y, int mx, int my) {
		int w = font.width(comp);
		int x = imageWidth - titleLabelX - w;
		y += titleLabelY;
		int h = 13;
		boolean ans = isHovering(x, y, w, h, mx, my);
		if (ans) {
			comp = comp.withStyle(ChatFormatting.ITALIC);
		}
		g.drawString(font, comp, x, y, 4210752, false);
		return ans;
	}

	@Override
	protected void renderBg(GuiGraphics poseStack, float ptick, int mx, int my) {
		var sr = menu.sprite.getRenderer(this);
		sr.start(poseStack);
	}

	@Override
	public boolean mouseClicked(double mx, double my, int btn) {
		if (hoverHostile) {
			menu.getConfig().resetHostile();
			return true;
		}
		if (hoverFriendly) {
			menu.getConfig().resetFriendly();
			return true;
		}
		return super.mouseClicked(mx, my, btn);
	}

}
