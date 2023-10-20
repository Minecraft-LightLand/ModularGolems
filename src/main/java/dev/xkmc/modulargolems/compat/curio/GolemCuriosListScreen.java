package dev.xkmc.modulargolems.compat.curio;

import dev.xkmc.l2library.base.menu.base.BaseContainerScreen;
import dev.xkmc.l2tabs.tabs.core.ITabScreen;
import dev.xkmc.l2tabs.tabs.core.TabManager;
import dev.xkmc.modulargolems.content.menu.registry.EquipmentGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import top.theillusivec4.curios.common.inventory.CurioSlot;

public class GolemCuriosListScreen extends BaseContainerScreen<GolemCuriosListMenu> implements ITabScreen {

	public GolemCuriosListScreen(GolemCuriosListMenu cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
	}

	@Override
	public void init() {
		super.init();
		var compat = CurioCompatRegistry.get();
		assert compat != null;
		new TabManager<>(this, new EquipmentGroup(menu.curios.golem)).init(this::addRenderableWidget, compat.tab);
	}

	@Override
	protected void renderBg(GuiGraphics g, float pTick, int mx, int my) {
		var sr = menu.sprite.get().getRenderer(this);
		sr.start(g);
		for (int i = 0; i < menu.curios.getSize(); i++) {
			sr.draw(g, "grid", "slot", i % 9 * 18 - 1, i / 9 * 18 - 1);
		}
	}

	@Override
	protected void renderTooltip(GuiGraphics g, int mx, int my) {
		LocalPlayer clientPlayer = Minecraft.getInstance().player;
		if (clientPlayer != null && clientPlayer.inventoryMenu
				.getCarried().isEmpty() && this.getSlotUnderMouse() != null) {
			Slot slot = this.getSlotUnderMouse();

			if (slot instanceof CurioSlot slotCurio && !slot.hasItem()) {
				g.renderTooltip(font, Component.translatable(slotCurio.getSlotName()), mx, my);
			}
		}
		super.renderTooltip(g, mx, my);
	}

}
