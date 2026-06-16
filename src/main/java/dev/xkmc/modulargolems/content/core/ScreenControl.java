package dev.xkmc.modulargolems.content.core;

import dev.xkmc.l2library.base.menu.base.MenuLayoutConfig;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;

import java.util.List;

public abstract class ScreenControl<T extends AbstractGolemEntity<T, ?>> {

	public final EquipmentsMenu menu;
	public final MenuControl<T> ctrl;

	public ScreenControl(MenuControl<T> ctrl) {
		this.ctrl = ctrl;
		this.menu = ctrl.menu;
	}

	public abstract void render(MenuLayoutConfig.ScreenRenderer sr, GuiGraphics g, float pTick);

	public List<Component> addSlotTooltip(Slot hoveredSlot) {
		return List.of();
	}

}
