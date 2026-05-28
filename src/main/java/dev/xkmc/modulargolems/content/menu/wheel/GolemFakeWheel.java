
package dev.xkmc.modulargolems.content.menu.wheel;

import dev.xkmc.l2itemselector.wheel.WheelAdaptor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record GolemFakeWheel(ItemStack stack, Component text) implements WheelAdaptor<WheelAdaptor.Entry> {

	@Override
	public List<WheelAdaptor.Entry> getWheelContent() {
		return List.of();
	}

	@Override
	public int getIndex(Player player) {
		return 0;
	}

	@Override
	public void select(int i) {

	}

	@Override
	public void renderIcon(GuiGraphicsExtractor g, int x0, int y0, boolean left, float sideWidth, boolean hover) {
		float cx = left ? sideWidth / 2.0F : (float) g.guiWidth() - sideWidth / 2.0F;
		float r = Math.min((float) x0 / 1.5F, (float) y0) / 1.5F;
		float s = r * 0.015F * (hover ? 1.3f : 1);
		g.pose().pushMatrix();
		g.pose().translate(cx, (float) y0);
		g.pose().scale(s, s);
		g.item(stack, -8, -8);
		g.pose().popMatrix();
		int cx2 = left ? (int) (sideWidth / 2) : g.guiWidth() - (int) (sideWidth / 2);
		int ty = y0 + (int) (s * 10);
		var font = Minecraft.getInstance().font;
		for (var line : font.split(text, (int) sideWidth - 4)) {
			g.text(font, line, cx2 - font.width(line) / 2, ty, 0xffffffff, true);
			ty += font.lineHeight + 1;
		}
	}

}
