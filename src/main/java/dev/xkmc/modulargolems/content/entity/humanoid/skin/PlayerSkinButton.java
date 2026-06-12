package dev.xkmc.modulargolems.content.entity.humanoid.skin;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PlayerSkinButton extends Button {

	private static final ResourceLocation TEXTURE = ModularGolems.loc("textures/gui/sprites/button/skin.png");

	private final HumanoidGolemEntity golem;

	public PlayerSkinButton(int x, int y, HumanoidGolemEntity golem, OnPress onPress) {
		super(x + 2, y, 9, 9, Component.empty(), onPress, DEFAULT_NARRATION);
		this.golem = golem;
		setTooltip(Tooltip.create(Component.translatable(ModularGolems.MODID + ".tooltip.player_skin_button")));
	}

	@Override
	public void renderWidget(GuiGraphics g, int mx, int my, float pt) {
		int v = isHoveredOrFocused() ? 9 : 0;
		g.blit(TEXTURE, getX(), getY(), 0, v, 9, 9, 9, 18);
	}

	@Override
	public boolean mouseClicked(double mx, double my, int button) {
		if (this.active && this.visible && this.clicked(mx, my)) {
			if (button == 1) {
				this.playDownSound(Minecraft.getInstance().getSoundManager());
				ModularGolems.HANDLER.toServer(SetPlayerSkinToServer.of(golem.getId(), ""));
				golem.setPlayerSkin("");
				return true;
			}
		}
		return super.mouseClicked(mx, my, button);
	}

}
