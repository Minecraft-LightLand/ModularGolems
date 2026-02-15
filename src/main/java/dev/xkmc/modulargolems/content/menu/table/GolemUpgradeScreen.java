package dev.xkmc.modulargolems.content.menu.table;

import dev.xkmc.l2library.base.menu.base.BaseContainerScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GolemUpgradeScreen extends BaseContainerScreen<GolemUpgradeMenu> {

	private Button left, right;

	public GolemUpgradeScreen(GolemUpgradeMenu cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
	}

	@Override
	protected void renderBg(GuiGraphics g, float p_97788_, int p_97789_, int p_97790_) {
		var sr = menu.sprite.get().getRenderer(this);
		sr.start(g);
		updatePage();
	}

	@Override
	protected void init() {
		super.init();
		int w = 10;
		int h = 11;
		int x = (this.width + this.imageWidth) / 2 - 16;
		int y = (this.height - this.imageHeight) / 2 + 4;
		this.addRenderableWidget(left = Button.builder(Component.literal("<"), (e) -> this.click(-1))
				.pos(x - w - 1, y).size(w, h).build());
		this.addRenderableWidget(right = Button.builder(Component.literal(">"), (e) -> this.click(1))
				.pos(x, y).size(w, h).build());
		updatePage();
	}

	private void updatePage() {
		left.active = menu.page.get() > 0;
		right.active = menu.page.get() < menu.maxPage.get() - 1;
	}

}
