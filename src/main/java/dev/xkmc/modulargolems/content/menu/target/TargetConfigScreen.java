package dev.xkmc.modulargolems.content.menu.target;

import dev.xkmc.l2tabs.tabs.core.TabManager;
import dev.xkmc.modulargolems.content.menu.ghost.GhostItemScreen;
import dev.xkmc.modulargolems.content.menu.registry.ConfigGroup;
import dev.xkmc.modulargolems.content.menu.registry.GolemTabRegistry;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
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
		new TabManager<>(this, new ConfigGroup(menu.editor.editor()))
				.init(this::addRenderableWidget, GolemTabRegistry.CONFIG_TARGET.get());
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor g, int x, int y) {
		super.extractLabels(g, x, y);
		drawLeft(g, MGLangData.UI_TARGET_HOSTILE.get(), 13);
		drawLeft(g, MGLangData.UI_TARGET_FRIENDLY.get(), 62);
		hoverHostile = drawRight(g, MGLangData.UI_TARGET_RESET.get().withStyle(ChatFormatting.UNDERLINE), 13, x, y);
		hoverFriendly = drawRight(g, MGLangData.UI_TARGET_RESET.get().withStyle(ChatFormatting.UNDERLINE), 62, x, y);
	}

	private void drawLeft(GuiGraphicsExtractor g, Component comp, int y) {
		int x = titleLabelX;
		y += titleLabelY;
		g.text(font, comp, x, y, 4210752, false);
	}

	private boolean drawRight(GuiGraphicsExtractor g, MutableComponent comp, int y, int mx, int my) {
		int w = font.width(comp);
		int x = imageWidth - titleLabelX - w;
		y += titleLabelY;
		int h = 13;
		boolean ans = isHovering(x, y, w, h, mx, my);
		if (ans) {
			comp.withStyle(ChatFormatting.ITALIC);
		}
		g.text(font, comp, x, y, 4210752, false);
		return ans;
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor g, int mx, int my, float a) {
		super.extractBackground(g, mx, my, a);
		var sr = getRenderer();
		sr.start(g);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (hoverHostile) {
			menu.getConfig().resetHostile();
			return true;
		}
		if (hoverFriendly) {
			menu.getConfig().resetFriendly();
			return true;
		}
		return super.mouseClicked(event, doubleClick);
	}

}
