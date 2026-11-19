package dev.xkmc.modulargolems.content.menu.config;

import dev.xkmc.l2library.base.menu.base.BaseContainerScreen;
import dev.xkmc.modulargolems.content.menu.registry.ConfigGroup;
import dev.xkmc.modulargolems.content.menu.registry.GolemTabRegistry;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabManager;
import dev.xkmc.modulargolems.content.menu.tabs.ITabScreen;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleConsumer;

public class ParamConfigScreen extends BaseContainerScreen<ToggleGolemConfigMenu> implements ITabScreen {

	private record Row(int x, int y, int w, int h, Component label, List<Component> tooltip, EditBox box) {
	}

	private final List<Row> rows = new ArrayList<>();

	public ParamConfigScreen(ToggleGolemConfigMenu cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
	}

	@Override
	protected void init() {
		super.init();
		rows.clear();
		new GolemTabManager<>(this, new ConfigGroup(menu.editor))
				.init(this::addRenderableWidget, GolemTabRegistry.CONFIG_PARAM);
		int left = getGuiLeft();
		int top = getGuiTop();
		int width = getXSize();
		addRow(left, top, width, 40, MGLangData.CONFIG_FOLLOW_DISTANCE,
				MGLangData.CONFIG_FOLLOW_DISTANCE_TOOLTIP, menu.editor.getFollowDistance(),
				menu.editor::setFollowDistance, MGConfig.COMMON.startFollowRadius.get());
		addRow(left, top, width, 60, MGLangData.CONFIG_STOP_DISTANCE,
				MGLangData.CONFIG_STOP_DISTANCE_TOOLTIP, menu.editor.getStopDistance(),
				menu.editor::setStopDistance, 3);
		addRow(left, top, width, 80, MGLangData.CONFIG_TELEPORT_DISTANCE,
				MGLangData.CONFIG_TELEPORT_DISTANCE_TOOLTIP, menu.editor.getTeleportDistance(),
				menu.editor::setTeleportDistance, MGConfig.COMMON.maxWanderRadius.get());
		addRow(left, top, width, 100, MGLangData.CONFIG_WANDER_RADIUS,
				MGLangData.CONFIG_WANDER_RADIUS_TOOLTIP, menu.editor.getWanderRadius(),
				menu.editor::setWanderRadius, MGConfig.COMMON.stopWanderRadius.get());
	}

	private void addRow(int left, int top, int width, int rowY, MGLangData label, MGLangData tooltip,
						double value, DoubleConsumer setter, double defaultValue) {
		EditBox box = new EditBox(this.font, left + 7 + 126, top + rowY, width - 14 - 126, 16, Component.empty());
		box.setMaxLength(6);
		box.setValue(formatDistance(value));
		box.setResponder(s -> {
			try {
				double v = Double.parseDouble(s.trim());
				if (v >= 1 && v <= 1000) setter.accept(v);
			} catch (NumberFormatException ignored) {
			}
		});
		addRenderableWidget(box);
		rows.add(new Row(left + 7, top + rowY - 2, width - 14, 20, label.get(),
				List.of(tooltip.get(), MGLangData.CONFIG_DEFAULT_VALUE.get(formatDistance(defaultValue))), box));
	}

	@Override
	protected void renderBg(GuiGraphics g, float pTick, int mx, int my) {
		var sr = menu.sprite.get().getRenderer(this);
		sr.start(g);
		int count = menu.container.getContainerSize();
		var c = menu.sprite.get().getComp("hand");
		for (int i = 0; i < count; i++) {
			if (!menu.container.getItem(i).isEmpty()) continue;
			sr.draw(g, "hand", "altas_golem", (i % c.rx) * c.w, (i / c.rx) * c.h);
		}
		for (var row : rows) {
			g.drawString(this.font, row.label().getString(), row.x() + 2, row.y() + 6, 0x404040, false);
		}
	}

	/** 空傀儡槽位提示与通用配置页一致。 */
	@Override
	protected void renderTooltip(GuiGraphics g, int mx, int my) {
		if (hoveredSlot != null && hoveredSlot.container == menu.container && hoveredSlot.getItem().isEmpty()) {
			g.renderTooltip(this.font, MGLangData.CONFIG_SET.get(), mx, my);
			return;
		}
		super.renderTooltip(g, mx, my);
	}

	@Override
	public void render(GuiGraphics g, int mx, int my, float pt) {
		super.render(g, mx, my, pt);
		for (var row : rows) {
			if (mx >= row.x() && mx <= row.x() + row.w() && my >= row.y() && my <= row.y() + row.h()) {
				g.renderComponentTooltip(this.font, row.tooltip(), mx, my);
			}
		}
	}

	@Override
	public boolean mouseClicked(double mx, double my, int btn) {
		for (var row : rows) {
			EditBox box = row.box();
			if (box.isFocused() && !isOver(box, mx, my)) box.setFocused(false);
		}
		return super.mouseClicked(mx, my, btn);
	}

	@Override
	public boolean keyPressed(int key, int scan, int mods) {
		for (var row : rows) {
			EditBox box = row.box();
			if (!box.isFocused()) continue;
			if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
				box.setFocused(false);
				return true;
			}
			if (key == GLFW.GLFW_KEY_ESCAPE) {
				box.setFocused(false);
				return super.keyPressed(key, scan, mods);
			}
			if (box.keyPressed(key, scan, mods)) return true;
			if (key == GLFW.GLFW_KEY_TAB) box.setFocused(false);
			return true;
		}
		return super.keyPressed(key, scan, mods);
	}

	private static boolean isOver(EditBox box, double mx, double my) {
		return mx >= box.getX() && mx <= box.getX() + box.getWidth()
				&& my >= box.getY() && my <= box.getY() + box.getHeight();
	}

	private static String formatDistance(double d) {
		if (d == Math.rint(d)) return Long.toString((long) d);
		return String.format(java.util.Locale.ROOT, "%.1f", d);
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
