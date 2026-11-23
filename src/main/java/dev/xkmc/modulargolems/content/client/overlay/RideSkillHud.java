package dev.xkmc.modulargolems.content.client.overlay;

import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.ArrayList;
import java.util.List;

/**
 * 骑乘时的冲锋/大跳冷却面板，显示在屏幕左侧、竖直居中。
 * 按傀儡分组：先一行标题（大型傀儡 / 人形傀儡 …），下面缩进列出该傀儡的每只技能及冷却。
 * 这样能看出每个技能属于谁；多个同类技能都会逐条列出。
 */
public class RideSkillHud implements IGuiOverlay {

	private static final int ROW = 10;
	private static final int INDENT = 8;

	private record Row(String left, String right, int color, boolean header) {
	}

	/** group 存的是傀儡实体 id；据此拿到该傀儡自己的名字（自定义名或材质名）。 */
	private static String groupLabel(String group) {
		try {
			int id = Integer.parseInt(group);
			var level = Minecraft.getInstance().level;
			var ent = level != null ? level.getEntity(id) : null;
			if (ent != null) return ent.getName().getString();
		} catch (NumberFormatException ignored) {
		}
		return Component.translatable(group).getString();
	}

	@Override
	public void render(ForgeGui gui, GuiGraphics g, float partialTick, int screenWidth, int screenHeight) {
		if (Minecraft.getInstance().screen != null) return;
		LocalPlayer player = Proxy.getClientPlayer();
		if (player == null) return;
		if (!(player.getVehicle() instanceof AbstractGolemEntity<?, ?> golem)) return;
		if (!golem.hasPlayerPassenger()) return;
		ClientRideSkillCD.request(golem);
		List<GolemSkillCDToClient.Entry> list = ClientRideSkillCD.get(golem.getId());
		if (list.isEmpty()) return;

		Font font = gui.getFont();
		List<Row> rows = new ArrayList<>();
		String cur = null;
		for (var e : list) {
			String group = e.group();
			if (!group.isEmpty() && !group.equals(cur)) {
				cur = group;
				rows.add(new Row(groupLabel(group) + ":", "", 0xFFE0A000, true));
			}
			Component nameC = e.translate() ? Component.translatable(e.name()) : Component.literal(e.name());
			boolean ready = e.cd() <= 0;
			String cd = ready ? "\u2714" : ((e.cd() + 19) / 20) + "s";
			rows.add(new Row(nameC.getString(), cd, ready ? 0x55FF55 : 0xFFAA00, false));
		}
		if (rows.isEmpty()) return;

		int maxW = 0;
		for (var row : rows) {
			int w = font.width(row.left()) + (row.header() ? 0 : INDENT);
			if (!row.right().isEmpty()) w += 6 + font.width(row.right());
			maxW = Math.max(maxW, w);
		}
		int x0 = 6;
		int panelW = maxW + 8;
		int totalH = rows.size() * ROW;
		int y0 = screenHeight / 2 - totalH / 2;
		g.fill(x0 - 2, y0 - 2, x0 + panelW, y0 + totalH + 1, 0x90000000);
		int y = y0;
		for (var row : rows) {
			if (row.header()) {
				g.drawString(font, row.left(), x0 + 3, y, row.color(), true);
			} else {
				int tx = x0 + 3 + INDENT;
				g.drawString(font, row.left(), tx, y, 0xFFFFFF, true);
				if (!row.right().isEmpty()) {
					g.drawString(font, row.right(), x0 + panelW - 4 - font.width(row.right()), y, row.color(), true);
				}
			}
			y += ROW;
		}
	}

}
