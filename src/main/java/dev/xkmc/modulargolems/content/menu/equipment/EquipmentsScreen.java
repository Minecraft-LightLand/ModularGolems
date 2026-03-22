package dev.xkmc.modulargolems.content.menu.equipment;

import dev.xkmc.l2core.base.menu.base.BaseContainerScreen;
import dev.xkmc.l2tabs.tabs.core.ITabScreen;
import dev.xkmc.l2tabs.tabs.core.TabManager;
import dev.xkmc.modulargolems.content.entity.common.SweepGolemEntity;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.menu.registry.EquipmentGroup;
import dev.xkmc.modulargolems.content.menu.registry.GolemTabRegistry;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
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
		var sr = getRenderer();
		sr.start(g);
		if (menu.golem instanceof DogGolemEntity) {
			sr.draw(g, "head", "slot", -1, -1);
			sr.draw(g, "chest", "slot", -1, -1);
		} else {
			sr.draw(g, "right_hand", "slot", -1, -1);
			sr.draw(g, "left_hand", "slot", -1, -1);
			sr.draw(g, "head", "slot", -1, -1);
			sr.draw(g, "chest", "slot", -1, -1);
			sr.draw(g, "legs", "slot", -1, -1);
			sr.draw(g, "feet", "slot", -1, -1);
			if (menu.getAsPredSlot("left_hand", 0, 0).getItem().isEmpty())
				sr.draw(g, "left_hand", "altas_shield", 0, 0);
			if (menu.getAsPredSlot("right_hand", 0, 0).getItem().isEmpty())
				sr.draw(g, "right_hand", "slotbg_sword", -1, -1);
			if (menu.getAsPredSlot("head", 0, 0).getItem().isEmpty())
				sr.draw(g, "head", "altas_helmet", 0, 0);
			if (menu.getAsPredSlot("chest", 0, 0).getItem().isEmpty())
				sr.draw(g, "chest", "altas_chestplate", 0, 0);
			if (menu.getAsPredSlot("legs", 0, 0).getItem().isEmpty())
				sr.draw(g, "legs", "altas_leggings", 0, 0);
			if (menu.getAsPredSlot("feet", 0, 0).getItem().isEmpty())
				sr.draw(g, "feet", "altas_boots", 0, 0);
			if (menu.golem instanceof SweepGolemEntity<?, ?>) {
				sr.draw(g, "arrow", "slot", -1, -1);
				sr.draw(g, "backup", "slot", -1, -1);
				if (menu.getAsPredSlot("arrow", 0, 0).getItem().isEmpty())
					sr.draw(g, "arrow", "slotbg_arrow", -1, -1);
				if (menu.getAsPredSlot("backup", 0, 0).getItem().isEmpty())
					sr.draw(g, "backup", "slotbg_bow", -1, -1);
			}
			if (menu.golem instanceof MetalGolemEntity) {
				sr.draw(g, "left_shoulder", "slot", -1, -1);
				sr.draw(g, "right_shoulder", "slot", -1, -1);
				if (menu.getAsPredSlot("left_shoulder", 0, 0).getItem().isEmpty())
					sr.draw(g, "left_shoulder", "slotbg_shoulder", -1, -1);
				if (menu.getAsPredSlot("right_shoulder", 0, 0).getItem().isEmpty())
					sr.draw(g, "right_shoulder", "slotbg_shoulder", -1, -1);
			}
		}

		renderPreview(g, mx, my);

	}

	@Override
	protected void init() {
		super.init();
		new TabManager<>(this, new EquipmentGroup(menu.golem))
				.init(this::addRenderableWidget, GolemTabRegistry.EQUIPMENT.get());
	}

	@Override
	protected void renderTooltip(GuiGraphics g, int mx, int my) {
		super.renderTooltip(g, mx, my);
		if (menu.golem instanceof SweepGolemEntity<?,?> &&
				menu.getCarried().isEmpty() &&
				hoveredSlot != null && !hoveredSlot.hasItem()) {
			List<Component> list = null;
			if (menu.golem instanceof HumanoidGolemEntity) {
				if (hoveredSlot.getContainerSlot() == 0) {
					list = List.of(MGLangData.SLOT_MAIN.get(),
							MGLangData.SLOT_MAIN_DESC.get());
				}
				if (hoveredSlot.getContainerSlot() == 1) {
					list = List.of(MGLangData.SLOT_OFF.get());
				}
				if (hoveredSlot.getContainerSlot() == 6) {
					list = List.of(MGLangData.SLOT_BACKUP.get(),
							MGLangData.SLOT_BACKUP_DESC.get(),
							MGLangData.SLOT_BACKUP_INFO.get());
				}
				if (hoveredSlot.getContainerSlot() == 7) {
					list = List.of(MGLangData.SLOT_ARROW.get(),
							MGLangData.SLOT_ARROW_DESC.get());
				}
			}
			if (menu.golem instanceof MetalGolemEntity) {
				if (hoveredSlot.getContainerSlot() == 0) {
					list = List.of(MGLangData.SLOT_MAIN.get(),
							MGLangData.SLOT_MAIN_DESC_METAL.get());
				}
				if (hoveredSlot.getContainerSlot() == 1) {
					list = List.of(MGLangData.SLOT_OFF.get());
				}
				if (hoveredSlot.getContainerSlot() == 6) {
					list = List.of(MGLangData.SLOT_BACKUP.get(),
							MGLangData.SLOT_BACKUP_DESC.get(),
							MGLangData.SLOT_BACKUP_INFO.get());
				}
				if (hoveredSlot.getContainerSlot() == 7) {
					list = List.of(MGLangData.SLOT_ARROW.get(),
							MGLangData.SLOT_ARROW_DESC.get());
				}
				if (hoveredSlot.getContainerSlot() == 8) {
					list = List.of(MGLangData.SLOT_SHOULDER.get());
				}
				if (hoveredSlot.getContainerSlot() == 9) {
					list = List.of(MGLangData.SLOT_SHOULDER.get());
				}
			}
			if (list != null) {
				g.renderTooltip(this.font, list, Optional.empty(), ItemStack.EMPTY, mx, my);
			}
		}
	}

	private void renderPreview(GuiGraphics g, int mx, int my) {
		if (menu.golem == null) return;
		int x = leftPos + 30;
		int y = topPos + 80;
		double lx = x - mx;
		double ly = y - 40 - my;
		int scale = menu.golem instanceof MetalGolemEntity ? 18 :
				menu.golem instanceof HumanoidGolemEntity ? 24 :
						menu.golem instanceof DogGolemEntity ? 32 : 18;
		float ax = (float) Math.atan(lx / 50.0);
		float ay = (float) Math.atan(ly / 50.0);
		scale = (int) (scale / menu.golem.getScale());
		InventoryScreen.renderEntityInInventoryFollowsAngle(g,
				leftPos + 3, topPos + 16, leftPos + 58, topPos + 99,
				20, 1f / scale, ax, ay, menu.golem);

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
