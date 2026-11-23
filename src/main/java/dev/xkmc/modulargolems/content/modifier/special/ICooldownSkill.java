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

	/** 用于在回收/放置之间保存冷却的稳定键。 */
	default String getCooldownSaveKey() {
		String key = getCooldownNameKey();
		return key != null ? key : getClass().getName();
	}

	/**
	 * 把剩余冷却恢复成 ticks。只有把冷却存在"运行时字段"里的技能需要覆写；
	 * 存在 persistentData 时间戳里的技能默认不用做任何事。
	 */
	default void setRemainingCooldown(AbstractGolemEntity<?, ?> golem, int ticks) {
	}

}
