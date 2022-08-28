package dev.xkmc.modulargolems.content.upgrades;

import dev.xkmc.modulargolems.content.core.GolemModifier;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class UpgradeItem extends Item {

	private final Supplier<GolemModifier> modifier;

	public UpgradeItem(Properties props, Supplier<GolemModifier> modifier) {
		super(props);
		this.modifier = modifier;
	}

	public GolemModifier get() {
		return modifier.get();
	}

}
