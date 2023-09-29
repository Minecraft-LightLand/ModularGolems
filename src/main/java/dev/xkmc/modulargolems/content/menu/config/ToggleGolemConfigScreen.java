package dev.xkmc.modulargolems.content.menu.config;

import dev.xkmc.l2library.base.menu.base.BaseContainerScreen;
import dev.xkmc.modulargolems.content.entity.mode.GolemMode;
import dev.xkmc.modulargolems.content.entity.mode.GolemModes;
import dev.xkmc.modulargolems.content.menu.registry.ConfigGroup;
import dev.xkmc.modulargolems.content.menu.registry.GolemTabRegistry;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabManager;
import dev.xkmc.modulargolems.content.menu.tabs.ITabScreen;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ToggleGolemConfigScreen extends BaseContainerScreen<ToggleGolemConfigMenu> implements ITabScreen {

	public ToggleGolemConfigScreen(ToggleGolemConfigMenu cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
	}

	@Override
	protected void init() {
		super.init();
		new GolemTabManager<>(this, new ConfigGroup(menu.editor))
				.init(this::addRenderableWidget, GolemTabRegistry.CONFIG_TOGGLE);
		int left = getGuiLeft();
		int top = getGuiTop();
		int width = getXSize();
		addRenderableWidget(new CycleButton.Builder<>(GolemMode::getName)
				.withValues(GolemModes.LIST)
				.withInitialValue(menu.editor.getDefaultMode())
				.create(left + 7, top + 44, width - 14, 20,
						MGLangData.CONFIG_MODE.get(), this::modeChange));

		var pos = menu.editor.summonToPosition();
		var btn = CycleButton.booleanBuilder(
						MGLangData.CONFIG_TO_POSITION.get(),
						MGLangData.CONFIG_TO_TARGET.get())
				.withInitialValue(pos)
				.create(left + 7, top + 74, width - 14, 20,
						MGLangData.CONFIG_POS.get(), this::positionChange);
		updatePositionTooltip(btn, pos);
		addRenderableWidget(btn);
	}

	@Override
	protected void renderBg(GuiGraphics g, float pTick, int mx, int my) {
		var sr = menu.sprite.get().getRenderer(this);
		sr.start(g);
	}

	@Override
	protected void renderLabels(GuiGraphics g, int mx, int my) {
		super.renderLabels(g, mx, my);
		g.drawString(this.font, MGLangData.CONFIG_SET.get(), 32, 20, 4210752, false);
	}

	private void modeChange(CycleButton<GolemMode> button, GolemMode mode) {
		menu.editor.setDefaultMode(mode);
	}

	private void positionChange(CycleButton<Boolean> button, boolean bool) {
		menu.editor.setSummonToPosition(bool);
	}

	private void updatePositionTooltip(AbstractButton button, boolean bool) {
		button.setTooltip(Tooltip.create(bool ? MGLangData.CONFIG_TO_POSITION_TOOLTIP.get() : MGLangData.CONFIG_TO_TARGET_TOOLTIP.get()));
	}

	@Override
	public int screenWidth() {
		return getXSize();
	}

	@Override
	public int screenHeight() {
		return getYSize();
	}

}
