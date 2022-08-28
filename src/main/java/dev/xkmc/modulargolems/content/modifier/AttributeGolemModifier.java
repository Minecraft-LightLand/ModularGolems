package dev.xkmc.modulargolems.content.modifier;

import dev.xkmc.modulargolems.content.core.GolemModifier;
import dev.xkmc.modulargolems.content.core.GolemStatType;
import dev.xkmc.modulargolems.content.core.StatFilterType;

import java.util.function.Supplier;

public class AttributeGolemModifier extends GolemModifier {

	public record AttrEntry(Supplier<GolemStatType> type, double value) {

	}

	public final AttrEntry[] entries;

	public AttributeGolemModifier(int max, AttrEntry... entries) {
		super(StatFilterType.MASS, max);
		this.entries = entries;
	}

}
