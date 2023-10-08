package dev.xkmc.modulargolems.content.menu.tabs;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class GolemTabToken<G extends GolemTabGroup<G>, T extends GolemTabBase<G, T>> {

	public interface TabFactory<G extends GolemTabGroup<G>, T extends GolemTabBase<G, T>> {

		T create(int index, GolemTabToken<G, T> token, GolemTabManager<G> manager, ItemStack stack, Component component);

	}

	public final TabFactory<G, T> factory;
	public final GolemTabType type;

	private final Supplier<Item> item;
	private final Component title;

	public GolemTabToken(TabFactory<G, T> factory, Supplier<Item> item, Component component) {
		this.factory = factory;
		this.type = GolemTabType.RIGHT;

		this.item = item;
		this.title = component;
	}

	public T create(int index, GolemTabManager<G> manager) {
		return factory.create(index, this, manager, item.get().getDefaultInstance(), title);
	}

}
