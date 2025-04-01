package dev.xkmc.modulargolems.content.client.tracker;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2tabs.tabs.contents.BaseTextScreen;
import dev.xkmc.l2tabs.tabs.core.TabManager;
import dev.xkmc.modulargolems.content.capability.GolemConfigStorage;
import dev.xkmc.modulargolems.content.capability.GolemTracker;
import dev.xkmc.modulargolems.content.capability.TrackerDeleteToServer;
import dev.xkmc.modulargolems.content.capability.TrackerHeartBeatToServer;
import dev.xkmc.modulargolems.content.menu.tabs.ITabScreen;
import dev.xkmc.modulargolems.init.GolemClient;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public abstract class GolemInfoScreen extends BaseTextScreen implements ITabScreen {

	private static int linePerPage() {
		return 14;
	}

	private int page = 0, size = 0;
	private Button left, right;
	private boolean leftAdded, rightAdded;
	private UUID delId = null;

	protected GolemInfoScreen(Component title) {
		super(title, new ResourceLocation("l2tabs:textures/gui/empty.png"));
	}

	public List<Pair<UUID, GolemTracker.TrackedData>> getData(Predicate<GolemTracker.TrackedData> status) {
		var player = Minecraft.getInstance().player;
		if (player == null) return List.of();
		var tracker = GolemConfigStorage.get(player.level()).getTracker(player.getUUID());
		List<Pair<UUID, GolemTracker.TrackedData>> ans = new ArrayList<>();
		for (var e : tracker.data.entrySet()) {
			if (status.test(e.getValue())) {
				ans.add(Pair.of(e.getKey(), e.getValue()));
			}
		}
		ans.sort(Comparator.comparingLong(e -> -e.getSecond().timestamp));
		return ans;
	}

	public abstract List<Pair<UUID, GolemTracker.TrackedData>> getData();

	@Override
	public void init() {
		super.init();
		int x = (this.width + this.imageWidth) / 2 - 16;
		int y = (this.height - this.imageHeight) / 2 + 4;
		int w = 10;
		int h = 11;
		left = Button.builder(Component.literal("<"),
				(e) -> this.click(-1)).pos(x - w - 1, y).size(w, h).build();
		right = Button.builder(Component.literal(">"),
				(e) -> this.click(1)).pos(x, y).size(w, h).build();

		new TabManager(this).init(this::addRenderableWidget, GolemClient.TAB);
	}

	private void click(int offset) {
		page = page + offset;
		if (page < 0) page = 0;
		if (page >= (size - 1) / linePerPage() + 1) {
			page--;
		}
	}

	@Override
	public void render(GuiGraphics g, int mx, int my, float ptick) {
		var player = Minecraft.getInstance().player;
		if (player == null) return;
		long time = player.level().getGameTime();
		var data = getData();
		size = data.size();
		updateButtons();
		super.render(g, mx, my, ptick);
		int start = page * linePerPage();
		int max = Math.min((page + 1) * linePerPage(), size);
		int x = this.leftPos + 8;
		int y = this.topPos + 6;
		g.drawString(this.font, title, x, y, 0, false);
		y += 15;
		GolemTracker.TrackedData focus = null;
		int delLine = -1;
		delId = null;
		for (int i = 0; i < max - start; i++) {
			var ent = data.get(i + start);
			Component comp = TrackerInfo.getDesc(ent.getSecond());
			g.drawString(this.font, comp, x, y, 0, false);
			int w = Math.min(font.width(comp), imageWidth - 30);
			if (my > y && my < y + 10) {
				if (mx > x && mx < x + w) {
					focus = ent.getSecond();
				} else if (mx > x + imageWidth - 20 && mx < x + imageWidth - 8) {
					delLine = start + i;
					delId = ent.getFirst();
				}
			}
			var del = Component.literal("X").withStyle(delLine == start + i ? ChatFormatting.RED : ChatFormatting.BLACK);
			g.drawString(this.font, del, x + imageWidth - 20, y, 0, false);
			y += 10;
		}
		if (focus != null) {
			g.renderComponentTooltip(this.font, TrackerInfo.getDetail(focus, player, time), mx, my);
		}
	}

	@Override
	public void tick() {
		var player = Minecraft.getInstance().player;
		if (player == null) return;
		if (player.level().getGameTime() % 10 == 0) {
			ModularGolems.HANDLER.toServer(new TrackerHeartBeatToServer(player.getUUID()));
		}
	}

	@Override
	public boolean mouseClicked(double x, double y, int btn) {
		if (super.mouseClicked(x, y, btn)) return true;
		var player = Minecraft.getInstance().player;
		if (player == null) return false;
		if (delId != null && btn == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			ModularGolems.HANDLER.toServer(new TrackerDeleteToServer(player.getUUID(), delId));
			return true;
		}
		return false;
	}

	private void updateButtons() {
		int pageNum = (size - 1) / linePerPage() + 1;
		if (page > 0 && !leftAdded) {
			leftAdded = true;
			addRenderableWidget(left);
		}
		if (page <= 0 && leftAdded) {
			leftAdded = false;
			removeWidget(left);
		}
		if (page < pageNum - 1 && !rightAdded) {
			rightAdded = true;
			addRenderableWidget(right);
		}
		if (page >= pageNum - 1 && rightAdded) {
			rightAdded = false;
			removeWidget(right);
		}
	}


	@Override
	public int getGuiLeft() {
		return leftPos;
	}

	@Override
	public int getGuiTop() {
		return topPos;
	}

	@Override
	public int screenWidth() {
		return width;
	}

	@Override
	public int screenHeight() {
		return height;
	}

	@Override
	public int getXSize() {
		return imageWidth;
	}

	@Override
	public int getYSize() {
		return imageHeight;
	}

}
