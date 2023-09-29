package dev.xkmc.modulargolems.content.menu.ghost;

import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemConfigScreen extends AbstractContainerScreen<ItemConfigMenu> {

	public ItemConfigScreen(ItemConfigMenu cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
		this.imageHeight = this.menu.sprite.getHeight();
		this.inventoryLabelY = this.menu.sprite.getPlInvY() - 11;
	}

	public void render(GuiGraphics stack, int mx, int my, float partial) {
		super.render(stack, mx, my, partial);
		this.renderTooltip(stack, mx, my);
	}

	@Override
	protected void renderBg(GuiGraphics poseStack, float ptick, int mx, int my) {
		var sr = menu.sprite.getRenderer(this);
		sr.start(poseStack);
		var config = menu.getConfig();
		if (config.isBlacklist()) sr.draw(poseStack, "filter", "filter_on");
		if (config.isTagMatch()) sr.draw(poseStack, "match", "match_on");
	}

	@Override
	public boolean mouseClicked(double mx, double my, int btn) {
		if (menu.sprite.within("filter", mx - leftPos, my - topPos)) {
			menu.getConfig().toggleFilter();
			return true;
		}
		if (menu.sprite.within("match", mx - leftPos, my - topPos)) {
			menu.getConfig().toggleTag();
			return true;
		}
		return super.mouseClicked(mx, my, btn);
	}

	@Override
	protected void renderTooltip(GuiGraphics g, int mx, int my) {
		if (menu.getCarried().isEmpty()) {
			var c = menu.getConfig();
			if (menu.sprite.within("filter", mx - leftPos, my - topPos)) {
				g.renderTooltip(font, (c.isBlacklist() ? MGLangData.UI_BLACKLIST : MGLangData.UI_WHITELIST).get(), mx, my);
			}
			if (menu.sprite.within("match", mx - leftPos, my - topPos)) {
				g.renderTooltip(font, (c.isTagMatch() ? MGLangData.UI_MATCH_TAG : MGLangData.UI_MATCH_ITEM).get(), mx, my);
			}
		}
		super.renderTooltip(g, mx, my);
	}

	public List<ItemTarget> getTargets() {
		List<ItemTarget> ans = new ArrayList<>();
		var rect = menu.sprite.getComp("grid");
		for (int y = 0; y < rect.ry; y++) {
			for (int x = 0; x < rect.rx; x++) {
				int id = y * rect.rx + x;
				ans.add(new ItemTarget(rect.x + x * rect.w + leftPos, rect.y + y * rect.h + topPos, 16, 16, stack -> addGhost(id, stack)));
			}
		}
		return ans;
	}

	public void addGhost(int ind, ItemStack stack) {
		menu.setSlotContent(ind, stack);
		ModularGolems.HANDLER.toServer(new SetItemFilterToServer(ind, stack));
	}

}
