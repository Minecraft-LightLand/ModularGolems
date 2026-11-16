package dev.xkmc.modulargolems.compat.materials.legendarymonsters;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.TeleportBackstepHelper;

/**
 * 湮灭构造体专用瞬移后撤：沿用通用 {@link TeleportBackstepHelper} 的位移与 busy 锁语义，
 * 只把瞬移特效换成传奇怪物的 {@code TELEPORT_EFFECT} 粒子（Boss 原版表现）。
 */
public class LMTeleportBackstepHelper extends TeleportBackstepHelper {

	public LMTeleportBackstepHelper(AbstractGolemEntity<?, ?> golem) {
		super(golem);
	}

	@Override
	protected void playTeleportFx(double x, double y, double z) {
		LMProxy.teleportFxAt(golem.level(), x, y, z);
	}

}
