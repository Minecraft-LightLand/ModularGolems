package dev.xkmc.modulargolems.content.client.overlay;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 骑乘 HUD 用的技能冷却采集：按"坐骑 + 背上的乘客"分组，每组列出该傀儡的冲锋和大跳技能
 * （每只技能一条，含就绪的 0 秒）。group 用该傀儡的实体 id，客户端据此显示它自己的名字。
 */
public class RideSkillHelper {

	public static ArrayList<GolemSkillCDToClient.Entry> collect(AbstractGolemEntity<?, ?> mount) {
		List<AbstractGolemEntity<?, ?>> golems = new ArrayList<>();
		golems.add(mount);
		for (var e : mount.getPassengers()) {
			if (e instanceof AbstractGolemEntity<?, ?> p) golems.add(p);
		}
		ArrayList<GolemSkillCDToClient.Entry> ans = new ArrayList<>();
		long time = mount.level().getGameTime();
		for (var golem : golems) {
			// 用实体 id 当分组键，客户端再据此查出该傀儡自己的名字（含自定义名/材质描述）
			String group = String.valueOf(golem.getId());
			// 冲锋
			for (var e : golem.getModifiersExtended().entrySet()) {
				if (e.getKey() instanceof ChargeHelper.Modifier m) {
					long last = golem.getPersistentData().getLong(e.getKey().getID() + ":timestamp");
					int rem = last <= 0 ? 0 : (int) Math.max(0, last + m.getCooldown(golem, e.getValue()) - time);
					ans.add(new GolemSkillCDToClient.Entry(e.getKey().getDescriptionId(), rem, true, group));
				}
			}
			// 大跳
			for (var e : golem.getModifiersExtended().entrySet()) {
				if (e.getKey() instanceof EarthquakeHelper.Modifier m) {
					long last = golem.getPersistentData().getLong(e.getKey().getID() + ":timestamp");
					int rem = last <= 0 ? 0 : (int) Math.max(0, last + m.getCoolDown(golem, e.getValue()) - time);
					ans.add(new GolemSkillCDToClient.Entry(e.getKey().getDescriptionId(), rem, true, group));
				}
			}
		}
		return ans;
	}

}
