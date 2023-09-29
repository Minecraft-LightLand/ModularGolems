package dev.xkmc.modulargolems.content.menu.attribute;

import dev.xkmc.modulargolems.content.menu.registry.EquipmentGroup;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabBase;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabManager;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabToken;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class AttributeTab extends GolemTabBase<EquipmentGroup, AttributeTab> {

	public AttributeTab(int index, GolemTabToken<EquipmentGroup, AttributeTab> token, GolemTabManager<EquipmentGroup> manager, ItemStack stack, Component title) {
		super(index, token, manager, stack, title);
	}

	@Override
	public void onTabClicked() {
		Minecraft.getInstance().setScreen(new AttributeScreen(manager.token.golem, Component.literal("TODO")));//TODO
	}

}
