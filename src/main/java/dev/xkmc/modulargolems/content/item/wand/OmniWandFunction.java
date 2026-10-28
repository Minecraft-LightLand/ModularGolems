package dev.xkmc.modulargolems.content.item.wand;

import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * The five omnipotent wand functions, keyed by function rather than by item. An omnipotent wand carries exactly
 * one of these functions, and the bound key triggers that function regardless of which omnipotent wand is held.
 */
public enum OmniWandFunction {

	COMMAND,
	RETRIEVAL,
	SUMMON,
	RIDER,
	SQUAD;

	private static final OmniWandFunction[] VALUES = values();

	public static OmniWandFunction byOrdinal(int ordinal) {
		return ordinal >= 0 && ordinal < VALUES.length ? VALUES[ordinal] : null;
	}

	public static OmniWandFunction of(ItemStack stack) {
		return stack.isEmpty() ? null : of(stack.getItem());
	}

	public static OmniWandFunction of(Item item) {
		if (item == GolemItems.OMNI_COMMAND.get()) return COMMAND;
		if (item == GolemItems.OMNI_RETRIVAL.get()) return RETRIEVAL;
		if (item == GolemItems.OMNI_DISPENSE.get()) return SUMMON;
		if (item == GolemItems.OMNI_RIDER.get()) return RIDER;
		if (item == GolemItems.OMNI_SQUAD.get()) return SQUAD;
		return null;
	}

	public BaseWandItem item() {
		return switch (this) {
			case COMMAND -> GolemItems.OMNI_COMMAND.get();
			case RETRIEVAL -> GolemItems.OMNI_RETRIVAL.get();
			case SUMMON -> GolemItems.OMNI_DISPENSE.get();
			case RIDER -> GolemItems.OMNI_RIDER.get();
			case SQUAD -> GolemItems.OMNI_SQUAD.get();
		};
	}
}
