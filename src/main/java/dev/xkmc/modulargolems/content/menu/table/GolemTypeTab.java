package dev.xkmc.modulargolems.content.menu.table;

import dev.xkmc.modulargolems.content.menu.registry.GolemTypeGroup;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabBase;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabManager;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabToken;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * Left side tab that switches the golem type being assembled. It reuses the tab layout, tooltip and
 * selected highlight instead of drawing separate buttons.
 */
public class GolemTypeTab extends GolemTabBase<GolemTypeGroup, GolemTypeTab> {

	public GolemTypeTab(int index, GolemTabToken<GolemTypeGroup, GolemTypeTab> token,
			GolemTabManager<GolemTypeGroup> manager, ItemStack stack, Component title) {
		super(index, token, manager, stack, title);
	}

	@Override
	public void onTabClicked() {
		if (manager.getScreen() instanceof GolemAssembleScreen screen) {
			screen.selectType(index);
		}
	}

}
