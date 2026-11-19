package dev.xkmc.modulargolems.content.menu.config;

import dev.xkmc.modulargolems.content.menu.registry.ConfigGroup;
import dev.xkmc.modulargolems.content.menu.registry.OpenConfigMenuToServer;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabBase;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabManager;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabToken;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class ConfigToggleTab extends GolemTabBase<ConfigGroup, ConfigToggleTab> {

	public ConfigToggleTab(int index, GolemTabToken<ConfigGroup, ConfigToggleTab> token, GolemTabManager<ConfigGroup> manager, ItemStack stack, Component title) {
		super(index, token, manager, stack, title);
	}

	/** 图标用当前配置卡颜色的配置卡物品。 */
	@Override
	protected void renderIcon(GuiGraphics g) {
		int color = Math.floorMod(manager.token.editor.entry().getColor(), GolemItems.CARD.length);
		manager.type.drawIcon(g, getX(), getY(), index, new ItemStack(GolemItems.CARD[color].get()));
	}

	@Override
	public void onTabClicked() {
		var entry = manager.token.editor.entry();
		ModularGolems.HANDLER.toServer(new OpenConfigMenuToServer(entry.getID(), entry.getColor(), OpenConfigMenuToServer.Type.TOGGLE));
	}

}
