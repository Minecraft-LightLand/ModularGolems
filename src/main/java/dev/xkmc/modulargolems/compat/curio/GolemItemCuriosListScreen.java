package dev.xkmc.modulargolems.compat.curio;

import dev.xkmc.l2tabs.compat.BaseCuriosListScreen;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.menu.registry.EquipmentGroup;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabManager;
import dev.xkmc.modulargolems.content.menu.tabs.ITabScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GolemItemCuriosListScreen extends BaseCuriosListScreen<GolemItemCuriosListMenu> implements ITabScreen {

	public GolemItemCuriosListScreen(GolemItemCuriosListMenu cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
	}

	@Override
	public void init() {
		super.init();
		var compat = CurioCompatRegistry.get();
		assert compat != null;
		if (menu.curios.entity instanceof AbstractGolemEntity<?, ?> golem)
			new GolemTabManager<>(this, new EquipmentGroup(golem, menu.sourceStack, menu.sourceSlot))
					.init(this::addRenderableWidget, compat.tab);
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
