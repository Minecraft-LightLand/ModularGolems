package dev.xkmc.modulargolems.content.client.tracker;

import dev.xkmc.l2tabs.tabs.core.TabBase;
import dev.xkmc.l2tabs.tabs.core.TabManager;
import dev.xkmc.l2tabs.tabs.core.TabToken;
import dev.xkmc.modulargolems.content.menu.registry.TrackerGroup;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class TrackerTab extends TabBase<TrackerGroup, TrackerTab> {

	public enum Type {
		ALIVE(() -> new AliveGolemPage(MGLangData.TAB_ALIVE.get())),
		DEAD(() -> new DeadGolemPage(MGLangData.TAB_DEAD.get()));

		private final Supplier<GolemInfoScreen> factory;

		Type(Supplier<GolemInfoScreen> factory) {
			this.factory = factory;
		}

		public TrackerTab create(int index, TabToken<TrackerGroup, TrackerTab> token, TabManager<TrackerGroup> manager, Component title) {
			return new TrackerTab(this, index, token, manager, title);
		}

		public Screen createScreen() {
			return factory.get();
		}

	}

	private final Type tab;

	public TrackerTab(Type tab, int index, TabToken<TrackerGroup, TrackerTab> token, TabManager<TrackerGroup> manager, Component title) {
		super(index, token, manager, title);
		this.tab = tab;
	}

	@Override
	public void onTabClicked() {
		Minecraft.getInstance().setScreen(tab.createScreen());
	}

}
