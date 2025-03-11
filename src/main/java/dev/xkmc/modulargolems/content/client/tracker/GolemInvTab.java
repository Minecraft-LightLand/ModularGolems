package dev.xkmc.modulargolems.content.client.tracker;

import dev.xkmc.l2tabs.tabs.core.BaseTab;
import dev.xkmc.l2tabs.tabs.core.TabManager;
import dev.xkmc.l2tabs.tabs.core.TabToken;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class GolemInvTab extends BaseTab<GolemInvTab> {

	public GolemInvTab(TabToken<GolemInvTab> token, TabManager manager, ItemStack stack, Component title) {
		super(token, manager, stack, title);
	}

	public void onTabClicked() {
		Minecraft.getInstance().setScreen(TrackerTab.Type.ALIVE.createScreen());
	}

}
