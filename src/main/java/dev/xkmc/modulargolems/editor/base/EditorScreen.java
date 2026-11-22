package dev.xkmc.modulargolems.editor.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class EditorScreen extends Screen {

	protected EditorScreen(Component title) {
		super(title);
	}

	@Override
	public void resize(Minecraft mc, int w, int h) {
		this.width = w;
		this.height = h;
		this.rebuildWidgets();
	}

}
