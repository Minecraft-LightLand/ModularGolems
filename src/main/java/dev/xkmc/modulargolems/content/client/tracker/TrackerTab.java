package dev.xkmc.modulargolems.content.client.tracker;

import dev.xkmc.modulargolems.content.menu.registry.TrackerGroup;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabBase;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabManager;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabToken;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class TrackerTab extends GolemTabBase<TrackerGroup, TrackerTab> {

	public enum Type {
		ALIVE(() -> new AliveGolemPage(MGLangData.TAB_ALIVE.get())),
		DEAD(() -> new DeadGolemPage(MGLangData.TAB_DEAD.get()));

		private final Supplier<GolemInfoScreen> factory;

		Type(Supplier<GolemInfoScreen> factory) {
			this.factory = factory;
		}

		public TrackerTab create(int index, GolemTabToken<TrackerGroup, TrackerTab> token, GolemTabManager<TrackerGroup> manager, ItemStack stack, Component title) {
			return new TrackerTab(this, index, token, manager, stack, title);
		}

		public Screen createScreen() {
			return factory.get();
		}

	}

	private final Type tab;

	public TrackerTab(Type tab, int index, GolemTabToken<TrackerGroup, TrackerTab> token, GolemTabManager<TrackerGroup> manager, ItemStack stack, Component title) {
		super(index, token, manager, stack, title);
		this.tab = tab;
	}

	@Override
	public void onTabClicked() {
		Minecraft.getInstance().setScreen(tab.createScreen());
	}

}
