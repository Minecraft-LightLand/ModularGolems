package dev.xkmc.modulargolems.editor.base;

import net.minecraft.client.gui.components.Button;

import java.util.List;

public final class EditorLayout {

	private EditorLayout() {
	}

	public static void centerRow(List<Button> buttons, int centerX, int y, int gap) {
		int total = 0;
		for (Button b : buttons) {
			total += b.getWidth();
		}
		total += gap * Math.max(0, buttons.size() - 1);
		int x = centerX - total / 2;
		for (Button b : buttons) {
			b.setPosition(x, y);
			x += b.getWidth() + gap;
		}
	}

}
