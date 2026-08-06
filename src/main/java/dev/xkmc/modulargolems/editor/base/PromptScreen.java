package dev.xkmc.modulargolems.editor.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

public class PromptScreen extends EditorScreen {

	private final Component label;
	@Nullable
	private final String initial;
	private final Function<String, Component> validate;
	private final Consumer<String> callback;
	private final Screen parent;

	private EditBox box;
	@Nullable
	private Component error;

	public PromptScreen(Component title, Component label, @Nullable String initial,
						Function<String, Component> validate, Consumer<String> callback, Screen parent) {
		super(title);
		this.label = label;
		this.initial = initial;
		this.validate = validate;
		this.callback = callback;
		this.parent = parent;
	}

	@Override
	protected void init() {
		box = new EditBox(this.font, width / 2 - 100, height / 2 - 10, 200, 20, label);
		box.setValue(initial == null ? "" : initial);
		box.setMaxLength(256);
		box.setResponder(s -> error = null);
		addRenderableWidget(box);
		addRenderableWidget(Button.builder(EditorText.CANCEL.get(), b -> Minecraft.getInstance().setScreen(parent))
				.bounds(width / 2 - 104, height / 2 + 18, 100, 20).build());
		addRenderableWidget(Button.builder(EditorText.CONFIRM.get(), b -> submit())
				.bounds(width / 2 + 4, height / 2 + 18, 100, 20).build());
		setInitialFocus(box);
	}

	@Override
	public void onClose() {
		Minecraft.getInstance().setScreen(parent);
	}

	private void submit() {
		Component err = validate.apply(box.getValue());
		if (err != null) {
			error = err;
		} else {
			callback.accept(box.getValue());
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 257 || keyCode == 335) {
			submit();
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void render(GuiGraphics g, int mx, int my, float pTick) {
		super.renderBackground(g);
		super.render(g, mx, my, pTick);
		g.drawCenteredString(font, this.title, width / 2, height / 2 - 40, 0xFFFFFF);
		g.drawCenteredString(font, label, width / 2, height / 2 - 26, 0xAAAAAA);
		if (error != null) {
			g.drawCenteredString(font, error, width / 2, height / 2 + 42, 0xFF5555);
		}
	}

}
