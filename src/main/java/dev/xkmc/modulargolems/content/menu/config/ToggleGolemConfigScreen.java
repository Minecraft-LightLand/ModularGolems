package dev.xkmc.modulargolems.content.menu.config;

import dev.xkmc.l2library.base.menu.base.BaseContainerScreen;
import dev.xkmc.modulargolems.content.entity.mode.GolemMode;
import dev.xkmc.modulargolems.content.entity.mode.GolemModes;
import dev.xkmc.modulargolems.content.menu.registry.ConfigGroup;
import dev.xkmc.modulargolems.content.menu.registry.GolemTabRegistry;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabManager;
import dev.xkmc.modulargolems.content.menu.tabs.ITabScreen;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

public class ToggleGolemConfigScreen extends BaseContainerScreen<ToggleGolemConfigMenu> implements ITabScreen {

	private CycleButton<GolemMode> modeBtn;
	private CycleButton<Boolean> posBtn;
	private CycleButton<Boolean> lockBtn;

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
		// tooltip 必须走 withTooltip：CycleButton 每次切换都会用 tooltipSupplier 覆盖 setTooltip 的结果，
		// 手动 setTooltip 在第一次点击后就会被清空
		modeBtn = addRenderableWidget(new CycleButton.Builder<>(GolemMode::getName)
				.withValues(GolemModes.LIST)
				.withInitialValue(menu.editor.getDefaultMode())
				.withTooltip(mode -> Tooltip.create(MGLangData.CONFIG_MODE_TOOLTIP.get()))
				.create(left + 7, top + 40, width - 14, 20,
						MGLangData.CONFIG_MODE.get(), this::modeChange));

		posBtn = addRenderableWidget(CycleButton.booleanBuilder(
						MGLangData.CONFIG_TO_POSITION.get(),
						MGLangData.CONFIG_TO_TARGET.get())
				.withInitialValue(menu.editor.summonToPosition())
				.withTooltip(bool -> Tooltip.create(bool
						? MGLangData.CONFIG_TO_POSITION_TOOLTIP.get()
						: MGLangData.CONFIG_TO_TARGET_TOOLTIP.get()))
				.create(left + 7, top + 70, width - 14, 20,
						MGLangData.CONFIG_POS.get(), this::positionChange));

		lockBtn = addRenderableWidget(CycleButton.onOffBuilder(menu.editor.locked())
				.withTooltip(bool -> Tooltip.create(MGLangData.CONFIG_LOCK_TOOLTIP.get()))
				.create(left + 7, top + 100, width - 14, 20,
						MGLangData.CONFIG_LOCK.get(), this::lockChange));

	}

	/**
	 * 右键三个按钮 = 切到上一个值，与 shift + 左键等价。
	 * CycleButton 的反向切换是私有的，这里直接写入上一个值并同步 editor。
	 */
	@Override
	public boolean mouseClicked(double mx, double my, int btn) {
		if (btn == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			if (modeBtn != null && modeBtn.isMouseOver(mx, my)) {
				modeBtn.playDownSound(Minecraft.getInstance().getSoundManager());
				prevMode();
				return true;
			}
			if (posBtn != null && posBtn.isMouseOver(mx, my)) {
				posBtn.playDownSound(Minecraft.getInstance().getSoundManager());
				boolean bool = !menu.editor.summonToPosition();
				menu.editor.setSummonToPosition(bool);
				posBtn.setValue(bool);
				return true;
			}
			if (lockBtn != null && lockBtn.isMouseOver(mx, my)) {
				lockBtn.playDownSound(Minecraft.getInstance().getSoundManager());
				boolean bool = !menu.editor.locked();
				menu.editor.setLocked(bool);
				lockBtn.setValue(bool);
				return true;
			}
		}
		return super.mouseClicked(mx, my, btn);
	}

	private void prevMode() {
		int size = GolemModes.LIST.size();
		int i = GolemModes.LIST.indexOf(menu.editor.getDefaultMode());
		GolemMode prev = GolemModes.LIST.get(Mth.positiveModulo(i - 1, size));
		menu.editor.setDefaultMode(prev);
		modeBtn.setValue(prev);
	}

	@Override
	protected void renderBg(GuiGraphics g, float pTick, int mx, int my) {
		var sr = menu.sprite.get().getRenderer(this);
		sr.start(g);
		int count = menu.container.getContainerSize();
		// hand 是单格组件坐标，多格时要自己按格宽/格高偏移，否则只有第一格有图标
		var c = menu.sprite.get().getComp("hand");
		for (int i = 0; i < count; i++) {
			if (!menu.container.getItem(i).isEmpty()) continue;
			sr.draw(g, "hand", "altas_golem", (i % c.rx) * c.w, (i / c.rx) * c.h);
		}
	}

	/**
	 * 悬停在空的傀儡槽位上时提示“将傀儡放进这里设置配置卡”。
	 * 只在空槽时提示：槽里有物品时走原版的物品 tooltip。
	 */
	@Override
	protected void renderTooltip(GuiGraphics g, int mx, int my) {
		if (hoveredSlot != null && hoveredSlot.container == menu.container && hoveredSlot.getItem().isEmpty()) {
			g.renderTooltip(this.font, MGLangData.CONFIG_SET.get(), mx, my);
			return;
		}
		super.renderTooltip(g, mx, my);
	}

	private void modeChange(CycleButton<GolemMode> button, GolemMode mode) {
		menu.editor.setDefaultMode(mode);
	}

	private void positionChange(CycleButton<Boolean> button, boolean bool) {
		menu.editor.setSummonToPosition(bool);
	}

	private void lockChange(CycleButton<Boolean> btn, Boolean lock) {
		menu.editor.setLocked(lock);
	}

	@Override
	public int screenWidth() {
		return width;
	}

	@Override
	public int screenHeight() {
		return height;
	}

}
