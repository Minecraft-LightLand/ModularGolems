package dev.xkmc.modulargolems.content.core;

import java.util.Locale;

public enum GolemSlot {
	UP, LEFT, MIDDLE, RIGHT, DOWN;

	public String slotName() {
		return "golem_" + name().toLowerCase(Locale.ROOT);
	}
}
