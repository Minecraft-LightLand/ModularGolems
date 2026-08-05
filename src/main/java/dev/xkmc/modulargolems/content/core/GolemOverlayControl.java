package dev.xkmc.modulargolems.content.core;

import dev.xkmc.modulargolems.content.client.overlay.GolemStatusOverlay;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class GolemOverlayControl<T extends AbstractGolemEntity<T, ?>> {

	public final T golem;

	public GolemOverlayControl(T golem) {
		this.golem = golem;
	}

	public abstract void renderImage(GolemStatusOverlay.GolemEquipmentTooltip tooltip, Font font, int mx, int my, GuiGraphicsExtractor g);

	public abstract int getHeight(Font f);

	public abstract int getWidth(Font pFont);

}
