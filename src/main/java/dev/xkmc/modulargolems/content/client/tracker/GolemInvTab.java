package dev.xkmc.modulargolems.content.client.tracker;

import dev.xkmc.l2tabs.tabs.core.TabBase;
import dev.xkmc.l2tabs.tabs.core.TabManager;
import dev.xkmc.l2tabs.tabs.core.TabToken;
import dev.xkmc.l2tabs.tabs.inventory.InvTabData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class GolemInvTab extends TabBase<InvTabData, GolemInvTab> {

	public GolemInvTab(int index, TabToken<InvTabData, GolemInvTab> token, TabManager<InvTabData> manager, Component title) {
		super(index, token, manager, title);
	}

	public void onTabClicked() {
		Minecraft.getInstance().setScreen(TrackerTab.Type.ALIVE.createScreen());
	}

}
