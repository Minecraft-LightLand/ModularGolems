package dev.xkmc.modulargolems.content.menu.table;

import dev.xkmc.l2library.base.menu.base.BaseContainerScreen;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.ClientHolderManager;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.menu.registry.GolemTabRegistry;
import dev.xkmc.modulargolems.content.menu.registry.TableGroup;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabManager;
import dev.xkmc.modulargolems.content.menu.tabs.ITabScreen;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class GolemDisinegrateScreen extends BaseContainerScreen<GolemDisintegrateMenu> implements ITabScreen {

	private Button disintegrate;

	public GolemDisinegrateScreen(GolemDisintegrateMenu cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
	}

	@Override
	protected void init() {
		super.init();
		new GolemTabManager<>(this, new TableGroup())
				.init(this::addRenderableWidget, GolemTabRegistry.TABLE_DISINTEGRATE);

		var ref = menu.sprite.get().getComp("button");
		this.addRenderableWidget(disintegrate = Button.builder(Component.literal("X"), (e) -> this.click(1))
				.pos(leftPos + ref.x - 1, topPos + ref.y - 1).size(18, 18).build());
	}

	@Override
	protected void renderBg(GuiGraphics g, float pt, int mx, int my) {
		var sr = menu.sprite.get().getRenderer(this);
		sr.start(g);
		for (var e : menu.partSlots) {
			if (e.isActive()) {
				sr.draw(g, e.name, "slot", -1, -1);
				if (e.getItem().isEmpty() && !e.partShadow.isEmpty()) {
					int x = leftPos + e.x;
					int y = topPos + e.y;
					g.renderItem(e.partShadow, x, y, e.x + e.y * this.imageWidth);
					g.fillGradient(RenderType.guiOverlay(), x, y, x + 16, y + 16, 0x7f8B8B8B, 0x7f8B8B8B, 0);
				}
			}
		}
		if (menu.result.isActive())
			sr.draw(g, "result", "result_slot", -5, -5);
		boolean mayBreak = !menu.main.getItem().isEmpty();
		for (var e : menu.partSlots)
			mayBreak &= e.getItem().isEmpty();
		disintegrate.active = mayBreak;
		ItemStack preview = menu.result.getItem().isEmpty() ? menu.main.getItem() : menu.result.getItem();
		if (preview.getItem() instanceof GolemHolder<?, ?> holder) {
			int max = getLeftExpansion();
			g.pose().pushPose();
			AbstractGolemEntity<?, ?> golem = ClientHolderManager.getEntityForDisplay(holder, preview);
			if (golem != null) {
				int x = leftPos - 5 - max;
				int y = topPos + (imageHeight + max) / 2;
				double lx = leftPos - 5 - max / 2d - mx;
				double ly = topPos + imageHeight / 2d - my;
				int size = holder.getEntityType().values().length - 1;
				int scale = (int) (1d * max / size);
				float ax = (float) Math.atan(lx / max * 2);
				float ay = (float) Math.atan(ly / max * 2);
				InventoryScreen.renderEntityInInventoryFollowsAngle(g,
						x + max / 2, y, scale, ax, ay, golem);
			}
			g.pose().popPose();
		}
	}

	protected void renderTooltip(GuiGraphics g, int x, int y) {
		if (disintegrate.isHovered() && !menu.main.dropList.isEmpty()) {
			var list = menu.main.dropList;
			var item = menu.main.getItem();
			if (list.isEmpty()) {
				g.renderTooltip(font,
						List.of(MGLangData.UI_DISINTEGRATE.get()),
						Optional.empty(),
						item, x, y);
			} else if (list.size() > 54) {
				g.renderTooltip(font,
						List.of(MGLangData.UI_RETURN_MANY.get(list.size())),
						Optional.empty(),
						item, x, y);
			} else {
				g.renderTooltip(font,
						List.of(MGLangData.UI_RETURN_ITEMS.get()),
						Optional.of(new ItemListTooltip(list)),
						item, x, y);
			}
			return;
		}
		if (this.menu.getCarried().isEmpty() && hoveredSlot != null && hoveredSlot.getItem().isEmpty()) {
			if (hoveredSlot instanceof GolemDisintegrateMenu.PartSlot slot && !slot.partShadow.isEmpty()) {
				var stack = slot.partShadow;
				g.renderTooltip(font, getTooltipFromContainerItem(stack), stack.getTooltipImage(), stack, x, y);
				return;
			}
		}
		super.renderTooltip(g, x, y);
	}

	@Override
	public int getLeftExpansion() {
		ItemStack golem = menu.main.getItem();
		if (golem.getItem() instanceof GolemHolder<?, ?> holder) {
			int size = holder.getEntityType().values().length - 1;
			return Math.min(size * 60, Math.min(imageHeight - 20, leftPos - 10)) & -2;
		}
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
