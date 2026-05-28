package dev.xkmc.modulargolems.content.entity.humanoid.skin;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class PlayerSkinInputScreen extends Screen {

	private static final Component TITLE = Component.translatable(ModularGolems.MODID + ".gui.player_skin");
	private static final Component CONFIRM = Component.translatable(ModularGolems.MODID + ".gui.player_skin.confirm");
	private static final Component CANCEL = Component.translatable(ModularGolems.MODID + ".gui.player_skin.cancel");

	private final HumanoidGolemEntity golem;
	private EditBox input;

	public PlayerSkinInputScreen(HumanoidGolemEntity golem) {
		super(TITLE);
		this.golem = golem;
	}

	@Override
	protected void init() {
		int cx = width / 2;
		int cy = height / 2;

		input = new EditBox(font, cx - 120, cy - 20, 240, 20, Component.empty());
		input.setMaxLength(256);
		input.setValue(golem.getPlayerSkin());
		addRenderableWidget(input);
		setInitialFocus(input);

		addRenderableWidget(Button.builder(CONFIRM, e -> confirm())
				.bounds(cx - 120, cy + 10, 116, 20).build());
		addRenderableWidget(Button.builder(CANCEL, e -> onClose())
				.bounds(cx + 4, cy + 10, 116, 20).build());
	}

	private void confirm() {
		String value = input.getValue().trim().replace('\\', '/');
		ModularGolems.HANDLER.toServer(SetPlayerSkinToServer.of(golem.getId(), value));
		golem.setPlayerSkin(value);
		onClose();
	}

	@Override
	public void render(GuiGraphics g, int mx, int my, float ptick) {
		renderBackground(g);
		super.render(g, mx, my, ptick);
		g.drawCenteredString(font, TITLE, width / 2, height / 2 - 40, 0xFFFFFF);
		g.drawCenteredString(font, Component.translatable(ModularGolems.MODID + ".gui.player_skin.hint"), width / 2, height / 2 + 40, 0x808080);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

}
