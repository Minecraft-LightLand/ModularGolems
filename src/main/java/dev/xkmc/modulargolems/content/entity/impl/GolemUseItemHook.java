package dev.xkmc.modulargolems.content.entity.impl;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Predicate;

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

	/**
	 * 这些物品的 UseTime 有专门的单写入者，客户端不应再走 Item#inventoryTick。
	 * 由对应兼容模块注册，避免公共代码直接引用可选 mod 的类。
	 */
	public static Predicate<ItemStack> SKIP_INVENTORY_TICK = stack -> false;

	private GolemUseItemHook() {
	}

}
