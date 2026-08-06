package dev.xkmc.modulargolems.editor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;

public class ReloadConfirmScreen extends Screen {

	private final Screen parent;
	private final Runnable onNow;
	private final Runnable onLater;

	public ReloadConfirmScreen(Screen parent, Runnable onNow, Runnable onLater) {
		super(EditorLang.RELOAD_TITLE.get());
		this.parent = parent;
		this.onNow = onNow;
		this.onLater = onLater;
	}

	@Override
	protected void init() {
		int c = width / 2;
		addRenderableWidget(Button.builder(EditorLang.RELOAD_NOW.get(), b -> onNow.run())
				.bounds(c - 155, height / 2 + 20, 140, 20).build());
		addRenderableWidget(Button.builder(EditorLang.LATER.get(), b -> onLater.run())
				.bounds(c + 15, height / 2 + 20, 100, 20).build());
	}

	@Override
	public void render(GuiGraphics g, int mx, int my, float pTick) {
		super.renderBackground(g);
		super.render(g, mx, my, pTick);
		g.drawCenteredString(font, this.title, width / 2, height / 2 - 24, 0xFFFFFF);
		g.drawCenteredString(font, EditorLang.RELOAD_NOTE.get(), width / 2, height / 2 - 6, 0xAAAAAA);
	}

	@Override
	public void onClose() {
		onLater.run();
	}

}
