package dev.xkmc.modulargolems.content.entity.impl;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link GolemUseItemImpl#tickClientUseItem()} 跑完之后的后置钩子。
 * <p>
 * 有些物品把运行时状态写在使用中那份 ItemStack 的 tag 里（射线枪的充能 / 蓄力进度 / 光束终点），
 * 而服务端那份 tag 不会自己变。需要把客户端 tag 推回服务端的兼容代码必须挂在这里——挂在 tick 开头
 * （如 {@code LivingTickEvent}）会拿到上一 tick 的值，服务端同步回来又把客户端拉回一 tick 前，
 * 形成“永远追不上”的死循环。
 */
public final class GolemUseItemHook {

	/**
	 * 物品使用姿势每 tick 处理一次。实现方自行判断手里的物品是否与自己相关。
	 */
	public interface Handler {

		void onItemUsePoseTick(AbstractGolemEntity<?, ?> golem);

	}

	/**
	 * 所有已注册的处理器，按注册顺序依次执行。
	 */
	public static final List<Handler> HANDLERS = new ArrayList<>();

	private GolemUseItemHook() {
	}

}
