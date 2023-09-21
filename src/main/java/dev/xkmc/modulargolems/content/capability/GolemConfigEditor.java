package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.modulargolems.content.entity.mode.GolemMode;
import dev.xkmc.modulargolems.content.entity.mode.GolemModes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

public record GolemConfigEditor(Level level, GolemConfigEntry entry) {

	public Component getDisplayName() {
		return entry.getDisplayName();
	}

	public GolemMode getDefaultMode() {
		return GolemModes.get(entry.defaultMode);
	}

	public void setDefaultMode(GolemMode mode) {
		entry.defaultMode = mode.getID();
		entry.sync(level);
	}

}
