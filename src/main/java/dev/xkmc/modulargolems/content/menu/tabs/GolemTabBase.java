package dev.xkmc.modulargolems.content.menu.tabs;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public abstract class GolemTabBase<G extends GolemTabGroup<G>, T extends GolemTabBase<G, T>> extends Button {

	public final int index;
	public final ItemStack stack;
	public final GolemTabToken<G, T> token;
	public final GolemTabManager<G> manager;

	@SuppressWarnings("unchecked")
	public GolemTabBase(int index, GolemTabToken<G, T> token, GolemTabManager<G> manager, ItemStack stack, Component title) {
		super(0, 0, 32, 28, title, b -> ((T) b).onTabClicked(), Supplier::get);
		this.index = index;
		this.stack = stack;
		this.token = token;
		this.manager = manager;
	}

	public abstract void onTabClicked();

	public void onTooltip(GuiGraphics g, int x, int y) {
		g.renderTooltip(Minecraft.getInstance().font, getMessage(), x, y);
	}

	public void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTicks) {
		if (this.visible) {
			token.type.draw(g, getX(), getY(), manager.selected == token, index);
			renderIcon(g);
		}
		if (this == manager.list.get(manager.list.size() - 1)) { // draw on last
			manager.onToolTipRender(g, mouseX, mouseY);
		}
	}

	protected void renderIcon(GuiGraphics g){
		token.type.drawIcon(g, getX(), getY(), index, this.stack);
	}

}
