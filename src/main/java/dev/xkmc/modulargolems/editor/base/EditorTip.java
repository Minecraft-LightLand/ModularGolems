package dev.xkmc.modulargolems.editor.base;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

/**
 * Small helper to attach a hover tooltip to an editor button.
 */
public final class EditorTip {

	private EditorTip() {
	}

	public static Button tip(Button button, @Nullable Component tooltip) {
		if (tooltip != null) button.setTooltip(Tooltip.create(tooltip));
		return button;
	}

}
