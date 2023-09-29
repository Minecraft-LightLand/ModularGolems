package dev.xkmc.modulargolems.content.menu.registry;

import dev.xkmc.modulargolems.content.menu.tabs.GolemTabBase;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabManager;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabToken;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class ConfigItemTab extends GolemTabBase<ConfigGroup, ConfigItemTab> {

	public ConfigItemTab(int index, GolemTabToken<ConfigGroup, ConfigItemTab> token, GolemTabManager<ConfigGroup> manager, ItemStack stack, Component title) {
		super(index, token, manager, stack, title);
	}

	@Override
	public void onTabClicked() {
		//TODO
	}

}
