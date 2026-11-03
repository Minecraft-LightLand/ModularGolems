package dev.xkmc.modulargolems.content.client.overlay;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.content.modifier.special.ICooldownSkill;

import java.util.ArrayList;

public class GolemSkillCDHelper {

	/**
	 * Collects the remaining cooldown of every skill currently on cooldown, in ticks.
	 * Jump/quake skills keep a per-entity timestamp here; everything else reports its own
	 * remaining cooldown through {@link ICooldownSkill}.
	 */
	public static ArrayList<GolemSkillCDToClient.Entry> collect(AbstractGolemEntity<?, ?> golem) {
		ArrayList<GolemSkillCDToClient.Entry> ans = new ArrayList<>();
		long time = golem.level().getGameTime();
		for (var e : golem.getModifiersExtended().entrySet()) {
			int rem;
			if (e.getKey() instanceof EarthquakeHelper.Modifier jump) {
				long last = golem.getPersistentData().getLong(e.getKey().getID() + ":timestamp");
				if (last <= 0 || last > time) continue;
				rem = (int) (last + jump.getCoolDown(golem, e.getValue()) - time);
			} else if (e.getKey() instanceof ICooldownSkill skill) {
				rem = skill.getRemainingCooldown(golem);
			} else {
				continue;
			}
			if (rem > 0) ans.add(new GolemSkillCDToClient.Entry(e.getKey().getDescriptionId(), rem, true));
		}
		for (var wg : golem.goalSelector.getAvailableGoals()) {
			if (!(wg.getGoal() instanceof ICooldownSkill skill)) continue;
			int rem = skill.getRemainingCooldown(golem);
			if (rem <= 0) continue;
			String key = skill.getCooldownNameKey();
			if (key != null) {
				ans.add(new GolemSkillCDToClient.Entry(key, rem, true));
			} else {
				ans.add(new GolemSkillCDToClient.Entry(skillName(wg.getGoal()), rem, false));
			}
		}
		return ans;
	}

	private static String skillName(Object goal) {
		String name = goal.getClass().getSimpleName();
		if (name.endsWith("AttackGoal")) name = name.substring(0, name.length() - "AttackGoal".length());
		else if (name.endsWith("Goal")) name = name.substring(0, name.length() - "Goal".length());
		return name.replaceAll("([a-z])([A-Z])", "$1 $2");
	}

}
