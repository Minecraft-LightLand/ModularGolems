package dev.xkmc.modulargolems.content.entity.impl;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;

/**
 * "大跳砸地"技能窗口：从起手到落地为 true，服务端维护、同步给客户端。
 * <p>
 * 不能用 {@code !onGround()} 代替：被击飞、从方块边缘走空、坐船等任何滞空状态都会命中，
 * 于是"只在大跳期间显示"的滞空特效会变成常驻（浮云傀儡的烟雾就是这么冒出来的）。
 */
public interface GolemLeapImpl extends GolemImplBase {

	default boolean isLeapActive() {
		return entityData().get(AbstractGolemEntity.LEAP_ACTIVE);
	}

	default void setLeapActive(boolean active) {
		entityData().set(AbstractGolemEntity.LEAP_ACTIVE, active);
	}

}
