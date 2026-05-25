package dev.xkmc.modulargolems.content.menu.wheel;

import dev.xkmc.l2itemselector.wheel.WheelAdaptor;
import dev.xkmc.modulargolems.content.entity.mode.GolemMode;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

public record GolemModeEntry(GolemMode mode) implements WheelAdaptor.Entry {

	@Override
	public void render(GuiGraphics g, float x0, float y0, float ai, float r0, float r, float da, boolean sel) {
		var s = sel ? 1.1f : 1;
		s *= Math.min(r * 0.015f, da * r0 / 16f);

		float dx = x0 + Mth.cos(ai) * r0;
		float dy = y0 + Mth.sin(ai) * r0;
		g.pose().pushPose();
		g.pose().translate(dx, dy, 0);
		g.pose().scale(s, s, s);
		renderIcon(g);
		g.pose().popPose();
	}

	public void renderIcon(GuiGraphics g) {
		var tex = ModularGolems.loc("mode/" + mode.key);
		g.blitSprite(tex, -8, -8, 16, 16);

	}

}
