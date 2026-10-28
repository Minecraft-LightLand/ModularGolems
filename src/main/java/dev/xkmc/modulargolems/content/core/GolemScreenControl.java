package dev.xkmc.modulargolems.content.core;

import dev.xkmc.l2library.base.menu.base.MenuLayoutConfig;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsMenu;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.client.gui.GuiGraphics;
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

	public abstract void render(MenuLayoutConfig.ScreenRenderer sr, GuiGraphics g, float pTick);

	/**
	 * Tooltips for the banner, skin and route slots. Every golem type has these three slots, so
	 * subclasses overriding this should fall back to super for the remaining names.
	 */
	public List<Component> addSlotTooltip(Slot hoveredSlot) {
		return switch (menu.getSlotNameAtMenuSlot(hoveredSlot.index)) {
			case "banner" -> List.of(MGLangData.SLOT_BANNER.get(), MGLangData.SLOT_BANNER_DESC.get());
			case "skin" -> List.of(MGLangData.SLOT_SKIN.get(), MGLangData.SLOT_SKIN_DESC.get());
			case "route" -> List.of(MGLangData.SLOT_ROUTE.get(), MGLangData.SLOT_ROUTE_DESC.get());
			default -> List.of();
		};
	}

}
