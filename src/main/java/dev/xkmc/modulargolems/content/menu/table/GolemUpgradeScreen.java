package dev.xkmc.modulargolems.content.menu.table;

import dev.xkmc.l2core.base.menu.base.BaseContainerScreen;
import dev.xkmc.l2core.util.GuiHelper;
import dev.xkmc.l2tabs.tabs.core.ITabScreen;
import dev.xkmc.modulargolems.content.item.upgrade.IUpgradeItem;
import dev.xkmc.modulargolems.content.item.upgrade.UpgradeItem;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class GolemUpgradeScreen extends BaseContainerScreen<GolemUpgradeMenu> implements ITabScreen {

	private Button left, right;

	public GolemUpgradeScreen(GolemUpgradeMenu cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float a) {
		super.extractBackground(g, mouseX, mouseY, a);
		var sr = getRenderer();
		sr.start(g);
		updatePage();
	}

	@Override
	protected void init() {
		super.init();
		TableTab.initScreen(TableTabType.UPGRADE, this, this::addRenderableWidget);

		int w = 11;
		int h = 11;
		int x = (this.width + this.imageWidth) / 2 - 70;
		int y = (this.height - this.imageHeight) / 2 + 27;
		this.addRenderableWidget(left = Button.builder(Component.empty(), (e) -> this.click(-1))
				.pos(x - w - 36, y).size(w, h).build(b -> new SpriteButton(b, "page/_prev")));
		this.addRenderableWidget(right = Button.builder(Component.empty(), (e) -> this.click(1))
				.pos(x, y).size(w, h).build(b -> new SpriteButton(b, "page/_next")));
		updatePage();
	}

	private void updatePage() {
		left.active = left.visible = menu.page.get() > 0;
		right.active = right.visible = menu.page.get() < menu.maxPage.get() - 1;
	}


	@Override
	protected void extractTooltip(GuiGraphicsExtractor g, int x, int y) {
		if (this.menu.getCarried().isEmpty() && hoveredSlot != null && hoveredSlot.hasItem()) {
			ItemStack stack = hoveredSlot.getItem();
			if (hoveredSlot instanceof UpgradeSlot) {
				if (stack.getItem() instanceof IUpgradeItem && !(stack.getItem() instanceof UpgradeItem)) {
					GuiHelper.tooltip(g, List.of(MGLangData.UI_REMOVE_TEMPLATE.get()), null, stack, x, y);
					return;
				}
				if (!hoveredSlot.mayPickup(menu.inventory.player)) {
					GuiHelper.tooltip(g, List.of(MGLangData.UI_NO_SLOT.get()), null, stack, x, y);
					return;
				}
			}
			var image = stack.getTooltipImage().map(ClientTooltipComponent::create).orElse(null);
			GuiHelper.tooltip(g, getTooltipFromContainerItem(stack), image, stack, x, y);
		}

	}

	public int getRightExpansion() {
		return 0;
	}

	@Override
	public int screenWidth() {
		return width;
	}

	@Override
	public int screenHeight() {
		return height;
	}

}
