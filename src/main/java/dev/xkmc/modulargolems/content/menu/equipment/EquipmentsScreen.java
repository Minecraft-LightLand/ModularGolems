package dev.xkmc.modulargolems.content.menu.equipment;

import dev.xkmc.l2library.base.menu.base.BaseContainerScreen;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.menu.registry.EquipmentGroup;
import dev.xkmc.modulargolems.content.menu.registry.GolemTabRegistry;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabManager;
import dev.xkmc.modulargolems.content.menu.tabs.ITabScreen;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class EquipmentsScreen extends BaseContainerScreen<EquipmentsMenu> implements ITabScreen {

	public EquipmentsScreen(EquipmentsMenu cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
	}

	@Override
	protected void renderBg(GuiGraphics g, float pTick, int mx, int my) {
		var sr = menu.sprite.get().getRenderer(this);
		sr.start(g);

		if (menu.getAsPredSlot("hand", 0, 1).getItem().isEmpty())
			sr.draw(g, "hand", "altas_shield", 0, 18);
		if (menu.getAsPredSlot("armor", 0, 0).getItem().isEmpty())
			sr.draw(g, "armor", "altas_helmet", 0, 0);
		if (menu.getAsPredSlot("armor", 0, 1).getItem().isEmpty())
			sr.draw(g, "armor", "altas_chestplate", 0, 18);
		if (menu.getAsPredSlot("armor", 0, 2).getItem().isEmpty())
			sr.draw(g, "armor", "altas_leggings", 0, 18 * 2);
		if (menu.getAsPredSlot("armor", 0, 3).getItem().isEmpty())
			sr.draw(g, "armor", "altas_boots", 0, 18 * 3);

		if (menu.golem instanceof HumanoidGolemEntity) {
			if (menu.getAsPredSlot("arrow", 0, 0).getItem().isEmpty())
				sr.draw(g, "arrow", "slotbg_arrow", -1, -1);
		}
	}

	@Override
	protected void init() {
		super.init();
		new GolemTabManager<>(this, new EquipmentGroup(menu.golem))
				.init(this::addRenderableWidget, GolemTabRegistry.EQUIPMENT);
	}

	@Override
	protected void renderTooltip(GuiGraphics g, int mx, int my) {
		super.renderTooltip(g, mx, my);
		if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null && !this.hoveredSlot.hasItem()) {
			List<Component> list = null;
			if (this.hoveredSlot.getContainerSlot() == 0) {
				list = List.of(MGLangData.SLOT_MAIN.get(),
						MGLangData.SLOT_MAIN_DESC.get());
			}
			if (this.hoveredSlot.getContainerSlot() == 1) {
				list = List.of(MGLangData.SLOT_OFF.get());
			}
			if (this.hoveredSlot.getContainerSlot() == 6) {
				list = List.of(MGLangData.SLOT_BACKUP.get(),
						MGLangData.SLOT_BACKUP_DESC.get(),
						MGLangData.SLOT_BACKUP_INFO.get());
			}
			if (this.hoveredSlot.getContainerSlot() == 7) {
				list = List.of(MGLangData.SLOT_ARROW.get(),
						MGLangData.SLOT_ARROW_DESC.get());

			}
			if (list != null) {
				g.renderTooltip(this.font, list, Optional.empty(), ItemStack.EMPTY, mx, my);
			}
		}
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
