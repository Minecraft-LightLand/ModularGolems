package dev.xkmc.modulargolems.content.entity.impl;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;

import java.util.function.Consumer;

/**
 * {@link GolemUseItemImpl#tickClientUseItem()} 跑完之后的后置钩子。
 * <p>
 * 有些物品把运行时状态写在使用中那份 ItemStack 的 tag 里（射线枪的充能 / 蓄力进度 / 光束终点），
 * 而服务端那份 tag 不会自己变。需要把客户端 tag 推回服务端的兼容代码必须挂在这里——
 * 挂在 tick 开头（如 {@code LivingTickEvent}）会拿到上一 tick 的值，服务端同步回来又把客户端
 * 拉回一 tick 前，形成"永远追不上"的死循环。
 */
public final class GolemUseItemHook {

	public static Consumer<AbstractGolemEntity<?, ?>> POST_TICK = e -> {
	};

	private GolemUseItemHook() {
	}

}
