package dev.xkmc.modulargolems.content.menu.equipment;

import dev.xkmc.l2library.base.menu.base.BaseContainerScreen;
import dev.xkmc.l2library.base.menu.base.PredSlot;
import dev.xkmc.modulargolems.content.core.GolemMenuControl;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.skin.PlayerSkinButton;
import dev.xkmc.modulargolems.content.entity.humanoid.skin.PlayerSkinInputScreen;
import dev.xkmc.modulargolems.content.menu.registry.EquipmentGroup;
import dev.xkmc.modulargolems.content.menu.registry.GolemTabRegistry;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabManager;
import dev.xkmc.modulargolems.content.menu.tabs.ITabScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
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
		var sr = menu.sprite.get().getRenderer(this);
		sr.start(g);
		Optional.ofNullable(menu.ctrl).flatMap(GolemMenuControl::getScreenProvider)
				.ifPresent(screenControl -> screenControl.render(sr, g, pTick));
		renderPreview(g, mx, my);
	}

	@Override
	protected void init() {
		super.init();
		if (menu.golem == null) return;
		new GolemTabManager<>(this, new EquipmentGroup(menu.golem))
				.init(this::addRenderableWidget, GolemTabRegistry.EQUIPMENT);
		if (menu.golem instanceof HumanoidGolemEntity golem) {
			addRenderableWidget(new PlayerSkinButton(leftPos + 137, topPos + 5, golem, b ->
					Minecraft.getInstance().setScreen(new PlayerSkinInputScreen(golem))));
		}
	}

	@Override
	protected void renderTooltip(GuiGraphics g, int mx, int my) {
		super.renderTooltip(g, mx, my);
		if (menu.getCarried().isEmpty() && hoveredSlot instanceof PredSlot && !hoveredSlot.hasItem()) {
			List<Component> list = Optional.ofNullable(menu.ctrl).flatMap(GolemMenuControl::getScreenProvider)
					.map(ctrl -> ctrl.addSlotTooltip(hoveredSlot)).orElse(List.of());
			if (!list.isEmpty()) {
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
		int scale = menu.golem.getPreviewScale();
		float ax = (float) Math.atan(lx / 50.0);
		float ay = (float) Math.atan(ly / 50.0);
		scale = (int) (scale / menu.golem.getScale());
		InventoryScreen.renderEntityInInventoryFollowsAngle(g, x, y, scale, ax, ay, menu.golem);

	}

	@Override
	public int screenWidth() {
		return width;
	}

	@Override
	public int screenHeight() {
		return height;
	}

	public <T extends GuiEventListener & Renderable & NarratableEntry> T addSkinWidget(T pWidget) {
		return addRenderableWidget(pWidget);
	}

}
