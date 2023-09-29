package dev.xkmc.modulargolems.content.menu.registry;

import dev.xkmc.modulargolems.content.menu.tabs.GolemTabBase;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabManager;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabToken;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class ConfigToggleTab extends GolemTabBase<ConfigGroup, ConfigToggleTab> {

	public ConfigToggleTab(int index, GolemTabToken<ConfigGroup, ConfigToggleTab> token, GolemTabManager<ConfigGroup> manager, ItemStack stack, Component title) {
		super(index, token, manager, stack, title);
	}

	@Override
	public void onTabClicked() {
		//TODO
	}

}
