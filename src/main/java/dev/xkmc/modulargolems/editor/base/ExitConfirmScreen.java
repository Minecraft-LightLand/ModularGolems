package dev.xkmc.modulargolems.editor.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;

public class ExitConfirmScreen extends Screen {

	private final Screen parent;
	private final Runnable onSave;
	private final Runnable onDiscard;

	public ExitConfirmScreen(Screen parent, Runnable onSave, Runnable onDiscard) {
		super(EditorText.UNSAVED_TITLE.get());
		this.parent = parent;
		this.onSave = onSave;
		this.onDiscard = onDiscard;
	}

	@Override
	protected void init() {
		int c = width / 2;
		addRenderableWidget(Button.builder(EditorText.SAVE.get(), b -> onSave.run())
				.bounds(c - 160, height / 2 + 20, 100, 20).build());
		addRenderableWidget(Button.builder(EditorText.DISCARD.get(), b -> onDiscard.run())
				.bounds(c - 50, height / 2 + 20, 100, 20).build());
		addRenderableWidget(Button.builder(EditorText.CANCEL.get(), b -> Minecraft.getInstance().setScreen(parent))
				.bounds(c + 60, height / 2 + 20, 100, 20).build());
	}

	@Override
	public void render(GuiGraphics g, int mx, int my, float pTick) {
		super.renderBackground(g);
		super.render(g, mx, my, pTick);
		g.drawCenteredString(font, this.title, width / 2, height / 2 - 24, 0xFFFFFF);
		g.drawCenteredString(font, EditorText.UNSAVED_NOTE.get(), width / 2, height / 2 - 6, 0xAAAAAA);
	}

	@Override
	public void onClose() {
		Minecraft.getInstance().setScreen(parent);
	}

}
