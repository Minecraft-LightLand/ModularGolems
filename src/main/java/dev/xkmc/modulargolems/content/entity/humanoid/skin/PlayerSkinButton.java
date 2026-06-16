package dev.xkmc.modulargolems.content.entity.humanoid.skin;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class PlayerSkinButton extends Button {

	private static final Identifier TEXTURE = ModularGolems.loc("textures/gui/sprites/button/skin.png");

	private final HumanoidGolemEntity golem;

	public PlayerSkinButton(int x, int y, HumanoidGolemEntity golem, OnPress onPress) {
		super(x + 2, y, 9, 9, Component.empty(), onPress, DEFAULT_NARRATION);
		this.golem = golem;
		setTooltip(Tooltip.create(Component.translatable(ModularGolems.MODID + ".tooltip.player_skin_button")));
	}

	@Override
	protected void extractContents(GuiGraphicsExtractor g, int mouseX, int mouseY, float a) {
		int v = isHoveredOrFocused() ? 9 : 0;
		g.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, getX(), getY(), 0, v, 9, 9, 9, 18);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (this.active && this.visible && this.isMouseOver(event.x(), event.y())) {
			if (event.button() == 1) {
				this.playDownSound(Minecraft.getInstance().getSoundManager());
				ModularGolems.HANDLER.toServer(SetPlayerSkinToServer.of(golem.getId(), ""));
				golem.setPlayerSkin("");
				return true;
			}
		}
		return super.mouseClicked(event, doubleClick);
	}

}
