package dev.xkmc.modulargolems.content.core;

import dev.xkmc.l2core.base.menu.base.LayoutRenderer;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;

import java.util.List;

public abstract class GolemScreenControl<T extends AbstractGolemEntity<T, ?>> {

	public final EquipmentsMenu menu;
	public final GolemMenuControl<T> ctrl;

	public GolemScreenControl(GolemMenuControl<T> ctrl) {
		this.ctrl = ctrl;
		this.menu = ctrl.menu;
	}

	public abstract void render(LayoutRenderer sr, GuiGraphicsExtractor g, float pTick);

	public List<Component> addSlotTooltip(Slot hoveredSlot) {
		return List.of();
	}

}
