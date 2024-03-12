package dev.xkmc.modulargolems.content.menu.path;

import dev.xkmc.modulargolems.content.menu.ghost.GhostItemScreen;
import dev.xkmc.modulargolems.content.menu.registry.ConfigGroup;
import dev.xkmc.modulargolems.content.menu.registry.GolemTabRegistry;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class PathConfigScreen extends GhostItemScreen<PathConfigMenu> {

	public PathConfigScreen(PathConfigMenu cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
	}

	@Override
	protected void init() {
		super.init();
		new GolemTabManager<>(this, new ConfigGroup(menu.editor.editor()))
				.init(this::addRenderableWidget, GolemTabRegistry.CONFIG_PATH);
	}

	@Override
	protected void renderBg(GuiGraphics poseStack, float ptick, int mx, int my) {
		var sr = menu.sprite.getRenderer(this);
		sr.start(poseStack);
	}

}
