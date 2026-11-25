package dev.xkmc.modulargolems.content.item.upgrade;

import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.base.ModifierInstance;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.function.Supplier;

public class SimpleUpgradeItem extends UpgradeItem {

	private final List<Supplier<GolemModifier>> modifiers;
	private final int level;

	public SimpleUpgradeItem(Item.Properties props, Supplier<GolemModifier> modifier, int level, boolean foil) {
		this(props, List.of(modifier), level, foil);
	}

	public SimpleUpgradeItem(Item.Properties props, List<Supplier<GolemModifier>> modifiers, int level, boolean foil) {
		super(props, foil);
		this.modifiers = modifiers;
		this.level = level;
	}

	public List<ModifierInstance> get() {
		return modifiers.stream().map(m -> new ModifierInstance(m.get(), level)).toList();
	}

}
