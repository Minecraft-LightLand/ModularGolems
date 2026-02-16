package dev.xkmc.modulargolems.content.menu.table;

import dev.xkmc.l2library.base.menu.base.BaseContainerScreen;
import dev.xkmc.modulargolems.content.item.upgrade.IUpgradeItem;
import dev.xkmc.modulargolems.content.item.upgrade.UpgradeItem;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

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


	protected void renderTooltip(GuiGraphics g, int x, int y) {
		if (this.menu.getCarried().isEmpty() && hoveredSlot != null && hoveredSlot.hasItem()) {
			ItemStack stack = hoveredSlot.getItem();
			if (stack.getItem() instanceof IUpgradeItem && !(stack.getItem() instanceof UpgradeItem))
				g.renderTooltip(font, List.of(MGLangData.UI_REMOVE_TEMPLATE.get()), Optional.empty(), stack, x, y);
			else if (!hoveredSlot.mayPickup(menu.inventory.player))
				g.renderTooltip(font, List.of(MGLangData.UI_NO_SLOT.get()), Optional.empty(), stack, x, y);
			else
				g.renderTooltip(font, getTooltipFromContainerItem(stack), stack.getTooltipImage(), stack, x, y);
		}

	}

}
