package dev.xkmc.modulargolems.content.menu.table;

import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;

public class SpriteButton extends Button {

	private final String id;

	private boolean pressed = false;

	protected SpriteButton(Builder builder, String id) {
		super(builder);
		this.id = id;
	}

	@Override
	public void onPress(InputWithModifiers input) {
		pressed = true;
	}

	@Override
	public void onRelease(MouseButtonEvent event) {
		if (!pressed) return;
		pressed = false;
		if (isMouseOver(event.x(), event.y())) {
			onPress.onPress(this);
		}
	}

	@Override
	protected void extractContents(GuiGraphicsExtractor g, int mx, int my, float a) {
		if (pressed & !isMouseOver(mx, my)) pressed = false;
		String tex = id;
		if (isActive()) {
			if (pressed) tex += "_down";
			else tex += "_on";
			if (isHoveredOrFocused())
				tex += "_hover";
		} else tex += "_ban";
		g.blitSprite(RenderPipelines.GUI_TEXTURED, ModularGolems.loc(tex), getX(), getY(), width, height);
	}

}
