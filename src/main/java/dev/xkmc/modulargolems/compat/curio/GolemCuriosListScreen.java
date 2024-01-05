package dev.xkmc.modulargolems.compat.curio;

import dev.xkmc.l2tabs.compat.BaseCuriosListScreen;
import dev.xkmc.l2tabs.tabs.core.ITabScreen;
import dev.xkmc.l2tabs.tabs.core.TabManager;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.menu.registry.EquipmentGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GolemCuriosListScreen extends BaseCuriosListScreen<GolemCuriosListMenu> implements ITabScreen {

	public GolemCuriosListScreen(GolemCuriosListMenu cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
	}

	@Override
	public void init() {
		super.init();
		var compat = CurioCompatRegistry.get();
		assert compat != null;
		new TabManager<>(this, new EquipmentGroup((AbstractGolemEntity<?, ?>) menu.curios.entity))
				.init(this::addRenderableWidget, compat.tab);
	}

}
