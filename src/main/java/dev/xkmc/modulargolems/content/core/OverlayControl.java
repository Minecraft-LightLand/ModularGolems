package dev.xkmc.modulargolems.content.core;

import dev.xkmc.modulargolems.content.client.overlay.GolemStatusOverlay;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public abstract class OverlayControl<T extends AbstractGolemEntity<T, ?>> {

	public final T golem;

	public OverlayControl(T golem) {
		this.golem = golem;
	}

	public abstract void renderImage(GolemStatusOverlay.GolemEquipmentTooltip tooltip, Font font, int mx, int my, GuiGraphics g);

	public abstract int getHeight();

	public abstract int getWidth(Font pFont);

}
