package dev.xkmc.modulargolems.content.menu.equipment;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFormAppearance;
import dev.xkmc.modulargolems.content.entity.common.SetFormAppearanceToServer;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * 复用人形傀儡装备界面的“切换皮肤”按钮外观（同一张贴图），点击在四种形态外观之间循环。
 */
public class FormAppearanceButton extends Button {

	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ModularGolems.MODID, "textures/gui/sprites/button/skin.png");

	private final AbstractGolemEntity<?, ?> golem;

	public FormAppearanceButton(int x, int y, AbstractGolemEntity<?, ?> golem) {
		super(x, y, 9, 9, Component.empty(), b -> cycle(golem, 1), DEFAULT_NARRATION);
		this.golem = golem;
	}

	private static void cycle(AbstractGolemEntity<?, ?> golem, int dir) {
		int next = Math.floorMod(golem.getFormAppearance() + dir, 4);
		golem.setFormAppearance(next);
		ModularGolems.HANDLER.toServer(SetFormAppearanceToServer.of(golem.getId(), next));
	}

	@Override
	public void onPress() {
		super.onPress();
		// 点击后清掉焦点，否则会一直停在“按下/高亮”外观
		this.setFocused(false);
	}

	@Override
	public boolean mouseClicked(double mx, double my, int button) {
		// 右键倒序切换
		if (button == 1 && this.active && this.visible && this.clicked(mx, my)) {
			this.playDownSound(Minecraft.getInstance().getSoundManager());
			cycle(golem, -1);
			this.setFocused(false);
			return true;
		}
		return super.mouseClicked(mx, my, button);
	}

	@Override
	public void renderWidget(GuiGraphics g, int mx, int my, float pt) {
		int mode = golem.getFormAppearance();
		setTooltip(Tooltip.create(Component.empty()
				.append(MGLangData.FORM_APPEARANCE_TITLE.get())
				.append("\n")
				.append(MGLangData.FORM_APPEARANCE_DESC.get())
				.append("\n")
				.append(MGLangData.FORM_APPEARANCE_CURRENT.get(GolemFormAppearance.modeName(mode)))));
		int v = isMouseOver(mx, my) ? 9 : 0;
		g.blit(TEXTURE, getX(), getY(), 0, v, 9, 9, 9, 18);
	}

}
