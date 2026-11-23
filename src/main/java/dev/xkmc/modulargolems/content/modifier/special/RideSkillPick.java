package dev.xkmc.modulargolems.content.modifier.special;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import org.jetbrains.annotations.Nullable;

/**
 * 玩家操控骑乘时，从"坐骑本身 + 骑在它身上的傀儡乘客"里挑一个可用的冲锋/大跳技能。
 * 先坐骑自己，再按乘客顺序；谁先有冷却好的就用谁。
 */
public final class RideSkillPick {

	private RideSkillPick() {
	}

	@Nullable
	public static ChargeHelper.Instance charge(AbstractGolemEntity<?, ?> mount) {
		ChargeHelper.Instance ins = ChargeHelper.findInstance(mount);
		if (ins != null) return ins;
		for (var e : mount.getPassengers()) {
			if (e instanceof AbstractGolemEntity<?, ?> p) {
				ins = ChargeHelper.findInstance(p);
				if (ins != null) return ins;
			}
		}
		return null;
	}

	@Nullable
	public static EarthquakeHelper.Instance jump(AbstractGolemEntity<?, ?> mount) {
		EarthquakeHelper.Instance ins = EarthquakeHelper.findMountInstance(mount);
		if (ins != null) return ins;
		for (var e : mount.getPassengers()) {
			if (e instanceof AbstractGolemEntity<?, ?> p) {
				ins = EarthquakeHelper.findMountInstance(p);
				if (ins != null) return ins;
			}
		}
		return null;
	}

}
