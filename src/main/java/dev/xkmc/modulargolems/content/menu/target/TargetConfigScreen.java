package dev.xkmc.modulargolems.content.menu.target;

import dev.xkmc.modulargolems.content.menu.ghost.GhostItemScreen;
import dev.xkmc.modulargolems.content.menu.registry.ConfigGroup;
import dev.xkmc.modulargolems.content.menu.registry.GolemTabRegistry;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabManager;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class TargetConfigScreen extends GhostItemScreen<TargetConfigMenu> {

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
		g.drawString(this.font, MGLangData.UI_TARGET_HOSTILE.get(), titleLabelX, titleLabelY + 13, 4210752, false);
		g.drawString(this.font, MGLangData.UI_TARGET_FRIENDLY.get(), titleLabelX, titleLabelY + 26 + 36, 4210752, false);
	}

	@Override
	protected void renderBg(GuiGraphics poseStack, float ptick, int mx, int my) {
		var sr = menu.sprite.getRenderer(this);
		sr.start(poseStack);
	}

}
