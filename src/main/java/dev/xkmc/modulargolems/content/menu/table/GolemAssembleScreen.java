package dev.xkmc.modulargolems.content.menu.table;

import dev.xkmc.l2library.base.menu.base.BaseContainerScreen;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.ClientHolderManager;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.menu.registry.GolemTypeGroup;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabManager;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabToken;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabType;
import dev.xkmc.modulargolems.content.menu.tabs.ITabScreen;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import dev.xkmc.modulargolems.util.GolemPreviewUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.lwjgl.glfw.GLFW;

public class GolemAssembleScreen extends BaseContainerScreen<GolemAssembleMenu> implements ITabScreen {

	private final List<GolemType<?, ?>> types = new ArrayList<>();
	private final List<GolemTabToken<GolemTypeGroup, ?>> typeTokens = new ArrayList<>();
	private SpriteButton batchButton;
	private GolemTabManager<GolemTypeGroup> typeTabs;
	private EditBox nameBox;

	public GolemAssembleScreen(GolemAssembleMenu cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
		for (var t : GolemTypes.TYPES.get())
			types.add(t);
		for (var t : types) {
			var rl = t.getRegistryName();
			typeTokens.add(new GolemTabToken<>(GolemTypeTab::new,
					() -> GolemType.getGolemHolder(t),
					Component.translatable("golem_type." + rl.getNamespace() + "." + rl.getPath())));
		}
	}

	/**
	 * Applies a golem type switch coming from a left side tab.
	 */
	public void selectType(int index) {
		this.click(index);
	}

	@Override
	protected void init() {
		super.init();
		TableTab.initScreen(TableTabType.ASSEMBLE, this, this::addRenderableWidget);
		// sits right of the golem slot; sent on focus loss so a whole name is not sent per keystroke
		nameBox = new EditBox(font, leftPos + 108, topPos + 22, 60, 12, MGLangData.UI_GOLEM_NAME.get());
		nameBox.setMaxLength(32);
		addRenderableWidget(nameBox);
		typeTabs = new GolemTabManager<>(this, new GolemTypeGroup(typeTokens), GolemTabType.LEFT, -1);
		typeTabs.init(this::addRenderableWidget, selectedTypeToken());
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

	private GolemTabToken<GolemTypeGroup, ?> selectedTypeToken() {
		int index = menu.selectedType.get();
		return index >= 0 && index < typeTokens.size() ? typeTokens.get(index) : null;
	}

	@Override
	public boolean mouseClicked(double mx, double my, int button) {
		// send on click away so a whole name goes over as one packet instead of one per keystroke
		if (nameBox != null && nameBox.isFocused()
				&& !isOver(nameBox, mx, my) && !nameBox.mouseClicked(mx, my, button)) {
			nameBox.setFocused(false);
			sendName();
		}
		return super.mouseClicked(mx, my, button);
	}

	@Override
	public boolean keyPressed(int key, int scan, int mods) {
		if (nameBox != null && nameBox.isFocused()) {
			if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
				nameBox.setFocused(false);
				sendName();
				return true;
			}
			if (nameBox.keyPressed(key, scan, mods)) return true;
			if (key == GLFW.GLFW_KEY_TAB) {
				nameBox.setFocused(false);
				sendName();
			}
			return true;
		}
		return super.keyPressed(key, scan, mods);
	}

	private void sendName() {
		if (nameBox != null)
			ModularGolems.HANDLER.toServer(new SetGolemNameToServer(nameBox.getValue()));
	}

	private static boolean isOver(AbstractWidget widget, double mx, double my) {
		return mx >= widget.getX() && mx <= widget.getX() + widget.getWidth()
				&& my >= widget.getY() && my <= widget.getY() + widget.getHeight();
	}

	@Override
	protected void renderBg(GuiGraphics g, float pt, int mx, int my) {
		// the selected type can change from the server, so keep the tab highlight live
		if (typeTabs != null) typeTabs.selected = selectedTypeToken();
		var sr = menu.sprite.get().getRenderer(this);
		sr.start(g);
		batchButton.visible = menu.result.isActive();
		sr.draw(g, "golem", "slot", -1, -1);
		if (menu.main.getItem().isEmpty()) {
			var selected = menu.getSelectedType();
			var level = menu.inventory.player.level();
			var template = selected == null ? GolemItems.GOLEM_TEMPLATE.get() : selected.getTemplate(level);
			drawShadow(g, menu.main, new ItemStack(template));
		}
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

	/**
	 * Override to nudge this screen's preview. Defaults to the shared values, where a zero in every
	 * field means nothing is overridden.
	 */
	protected GolemPreviewUtil.Adjustment previewAdjustment() {
		return new GolemPreviewUtil.Adjustment(-1, 8, -1, 8, 3);
	}

	private void renderPreview(GuiGraphics g, int mx, int my, ItemStack preview) {
		if (preview.getItem() instanceof GolemHolder<?, ?> holder) {
			AbstractGolemEntity<?, ?> golem = ClientHolderManager.getEntityForDisplay(holder, preview);
			if (golem != null) {
				GolemPreviewUtil.render(g, leftPos, topPos, golem, golem.getPreviewScale(),
						previewAdjustment(), mx, my);
			}
		}
	}

	@Override
	protected void renderTooltip(GuiGraphics g, int x, int y) {
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
	public int screenWidth() {
		return width;
	}

	@Override
	public int screenHeight() {
		return height;
	}

}
