package dev.xkmc.modulargolems.content.modifier.special;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;

/**
 * Implemented by any skill (goal or modifier) that has a cooldown worth showing on the wand panel.
 * Returns the remaining cooldown in ticks, zero when the skill is ready.
 */
public interface ICooldownSkill {

	int getRemainingCooldown(AbstractGolemEntity<?, ?> golem);

	/** Translation key for the display name, or null to fall back to the class name. */
	default String getCooldownNameKey() {
		return null;
	}

}
