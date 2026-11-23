package dev.xkmc.modulargolems.content.entity.impl;

import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.content.modifier.special.ICooldownSkill;
import net.minecraft.nbt.CompoundTag;

/**
 * 傀儡被回收成物品时保存各技能的剩余冷却，放置时再恢复。
 * 冷却有两个来源：冲锋/大跳这类存在实体 persistentData 时间戳里的，
 * 以及 {@link ICooldownSkill} 只存在 Goal 运行时字段里的（如 BaseRangedAttackGoal）。
 */
public interface GolemCooldownImpl extends GolemImplBase {

	default CompoundTag buildCooldownTag() {
		var self = asGolem();
		CompoundTag cd = new CompoundTag();
		long time = self.level().getGameTime();
		for (var e : self.getModifiersExtended().entrySet()) {
			String id = e.getKey().getID();
			if (e.getKey() instanceof EarthquakeHelper.Modifier m) {
				long last = self.getPersistentData().getLong(id + ":timestamp");
				int rem = (int) Math.max(0, last + m.getCoolDown(self, e.getValue()) - time);
				if (rem > 0) cd.putInt("e:" + id, rem);
			} else if (e.getKey() instanceof ChargeHelper.Modifier m) {
				long last = self.getPersistentData().getLong(id + ":timestamp");
				int rem = (int) Math.max(0, last + m.getCooldown(self, e.getValue()) - time);
				if (rem > 0) cd.putInt("c:" + id, rem);
			}
		}
		for (var wg : self.goalSelector.getAvailableGoals()) {
			if (wg.getGoal() instanceof ICooldownSkill skill) {
				int rem = skill.getRemainingCooldown(self);
				if (rem > 0) cd.putInt("g:" + skill.getCooldownSaveKey(), rem);
			}
		}
		return cd;
	}

	default void applyCooldowns(CompoundTag cd) {
		if (cd == null || cd.isEmpty()) return;
		var self = asGolem();
		long time = self.level().getGameTime();
		for (var e : self.getModifiersExtended().entrySet()) {
			String id = e.getKey().getID();
			if (e.getKey() instanceof EarthquakeHelper.Modifier m) {
				String key = "e:" + id;
				if (cd.contains(key)) {
					self.getPersistentData().putLong(id + ":timestamp", time + cd.getInt(key) - m.getCoolDown(self, e.getValue()));
				}
			} else if (e.getKey() instanceof ChargeHelper.Modifier m) {
				String key = "c:" + id;
				if (cd.contains(key)) {
					self.getPersistentData().putLong(id + ":timestamp", time + cd.getInt(key) - m.getCooldown(self, e.getValue()));
				}
			}
		}
		for (var wg : self.goalSelector.getAvailableGoals()) {
			if (wg.getGoal() instanceof ICooldownSkill skill) {
				String key = "g:" + skill.getCooldownSaveKey();
				if (cd.contains(key)) skill.setRemainingCooldown(self, cd.getInt(key));
			}
		}
	}

}
