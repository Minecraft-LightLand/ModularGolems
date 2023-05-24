package dev.xkmc.modulargolems.content.item;

import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.base.ModifierInstance;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.function.Supplier;

public class SimpleUpgradeItem extends UpgradeItem {

	private final Supplier<GolemModifier> modifier;
	private final int level;

	public SimpleUpgradeItem(Item.Properties props, Supplier<GolemModifier> modifier, int level, boolean foil) {
		super(props, foil);
		this.modifier = modifier;
		this.level = level;
	}

	public List<ModifierInstance> get() {
		return List.of(new ModifierInstance(modifier.get(), level));
	}

}
