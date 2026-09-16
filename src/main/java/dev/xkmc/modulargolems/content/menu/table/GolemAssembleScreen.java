package dev.xkmc.modulargolems.content.menu.table;

import dev.xkmc.l2library.base.menu.base.BaseContainerScreen;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.ClientHolderManager;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.menu.tabs.ITabScreen;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GolemAssembleScreen extends BaseContainerScreen<GolemAssembleMenu> implements ITabScreen {

	private final List<GolemType<?, ?>> types = new ArrayList<>();
	private SpriteButton[] typeButtons = new SpriteButton[0];
	private SpriteButton batchButton;

	public GolemAssembleScreen(GolemAssembleMenu cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
		for (var t : GolemTypes.TYPES.get())
			types.add(t);
		typeButtons = new SpriteButton[types.size()];
	}

	@Override
	protected void init() {
		super.init();
		TableTab.initScreen(TableTabType.ASSEMBLE, this, this::addRenderableWidget);
		for (int i = 0; i < types.size(); i++) {
			final int index = i;
typeButtons[i] = addRenderableWidget(new SpriteButton(
					Button.builder(Component.empty(), e -> this.click(index))
							.pos(leftPos + 108 + i * 21, topPos + 17).size(20, 20),
					menu.sprite.get(), "slot_button_on", "slot_button_down", "slot_button_ban") {
				@Override
				protected boolean getBan() {
					return menu.selectedType.get() == index;
				}

				@Override
				public void renderWidget(GuiGraphics g, int mx, int my, float pt) {
					super.renderWidget(g, mx, my, pt);
					var icon = GolemType.getGolemHolder(types.get(index)).getDefaultInstance();
					int ix = getX() + (getWidth() - 16) / 2;
					int iy = getY() + (getHeight() - 16) / 2;
					g.renderFakeItem(icon, ix, iy);
				}
			});
		}
		batchButton = addRenderableWidget(new SpriteButton(
				Button.builder(Component.empty(), e -> this.click(types.size()))
						.pos(leftPos + 122, topPos + 79).size(20, 20),
				menu.sprite.get(), "slot_button_on", "slot_button_down", "slot_button_ban") {
			@Override
			public void renderWidget(GuiGraphics g, int mx, int my, float pt) {
				super.renderWidget(g, mx, my, pt);
				var icon = menu.batchMode.get() == 1 ?
						new ItemStack(Items.SOUL_LANTERN) : new ItemStack(Items.LANTERN);
				int ix = getX() + (getWidth() - 16) / 2;
				int iy = getY() + (getHeight() - 16) / 2;
				g.renderFakeItem(icon, ix, iy);
			}
		});
	}

	@Override
	protected void renderBg(GuiGraphics g, float pt, int mx, int my) {
		var sr = menu.sprite.get().getRenderer(this);
		sr.start(g);
		batchButton.visible = menu.result.isActive();
		sr.draw(g, "golem", "slot", -1, -1);
		if (menu.main.getItem().isEmpty())
			drawShadow(g, menu.main, GolemItems.GOLEM_TEMPLATE.asStack());
		boolean any = false;
		for (var e : menu.partMatSlots)
			if (e != null && e.isActive())
				any = true;
		if (any) {
			sr.draw(g, "golem_part_background", "golem_part_background", -1, -1);
			for (var e : menu.partMatSlots) {
				if (e != null && e.isActive()) {
					if (e.isCore())
						sr.draw(g, e.slot.slotName(), "core_slot", -2, -2);
					else
						sr.draw(g, e.slot.slotName(), "slot", -1, -1);
					if (e.getItem().isEmpty())
						drawShadow(g, e, getPartShadow(e));
				}
			}
		}
		if (menu.result.isActive()) {
			sr.draw(g, "result", "result_slot", -5, -5);
			sr.draw(g, "arrow", "arrow_0", -3, 0);
			if (menu.result.getItem().isEmpty() && !menu.result.output.isEmpty())
				drawShadow(g, menu.result, menu.result.output);
		}
		var result = menu.result.getItem();
		if (result.isEmpty())
			result = menu.result.output;
		renderPreview(g, mx, my, result);
	}

	private ItemStack getPartShadow(GolemAssembleMenu.PartMatSlot e) {
		var type = menu.getSelectedType();
		if (type == null) return ItemStack.EMPTY;
		for (var part : type.values()) {
			if (part.getSlot() == e.slot)
				return part.toItem().getDefaultInstance();
		}
		return ItemStack.EMPTY;
	}

	private void drawShadow(GuiGraphics g, Slot e, ItemStack stack) {
		int x = leftPos + e.x;
		int y = topPos + e.y;
		g.renderItem(stack, x, y, e.x + e.y * this.imageWidth);
		g.fillGradient(RenderType.guiOverlay(), x, y, x + 16, y + 16, 0x7f8B8B8B, 0x7f8B8B8B, 0);
	}

	private void renderPreview(GuiGraphics g, int mx, int my, ItemStack preview) {
		if (preview.getItem() instanceof GolemHolder<?, ?> holder) {
			AbstractGolemEntity<?, ?> golem = ClientHolderManager.getEntityForDisplay(holder, preview);
			if (golem != null) {
				int x = leftPos + 30;
				int y = topPos + 80;
				double lx = x - mx;
				double ly = y - 40 - my;

				int scale = golem.getPreviewScale();
				float ax = (float) Math.atan(lx / 50.0);
				float ay = (float) Math.atan(ly / 50.0);
				InventoryScreen.renderEntityInInventoryFollowsAngle(g,
						x, y, scale, ax, ay, golem);
			}
		}
	}

	@Override
	protected void renderTooltip(GuiGraphics g, int x, int y) {
		for (int i = 0; i < typeButtons.length; i++) {
			if (typeButtons[i].isHovered()) {
				var type = types.get(i);
				var rl = type.getRegistryName();
				g.renderTooltip(font,
						List.of(Component.translatable("golem_type." + rl.getNamespace() + "." + rl.getPath())),
						Optional.empty(), ItemStack.EMPTY, x, y);
				return;
			}
		}
		if (batchButton != null && batchButton.isHovered()) {
			var text = menu.batchMode.get() == 1 ? MGLangData.UI_BATCH_ON.get() : MGLangData.UI_BATCH_OFF.get();
			g.renderTooltip(font, List.of(text), Optional.empty(), ItemStack.EMPTY, x, y);
			return;
		}
		if (this.menu.getCarried().isEmpty() && hoveredSlot != null && hoveredSlot.getItem().isEmpty()) {
			if (hoveredSlot instanceof GolemAssembleMenu.ResultSlot slot && slot.error != null) {
				g.renderTooltip(font, List.of(slot.error), Optional.empty(), ItemStack.EMPTY, x, y);
				return;
			}
			if (hoveredSlot == menu.main) {
				g.renderTooltip(font, List.of(MGLangData.UI_PUT_TEMPLATE.get()), Optional.empty(), ItemStack.EMPTY, x, y);
				return;
			}
			if (hoveredSlot instanceof GolemAssembleMenu.PartMatSlot) {
				g.renderTooltip(font, List.of(MGLangData.UI_PUT_PART_MAT.get()), Optional.empty(), ItemStack.EMPTY, x, y);
				return;
			}
		}
		super.renderTooltip(g, x, y);
	}

	@Override
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