package dev.xkmc.modulargolems.content.entity.impl;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

/**
 * 傀儡的"正在使用物品"状态。
 * <p>
 * vanilla 的 {@code DATA_LIVING_ENTITY_FLAGS} 不可靠：{@code startUsingItem} 只在服务端设 flag，
 * 而 {@code useItem} 字段不同步；客户端 {@code updatingUsingItem} 发现 {@code useItem} 为空就会
 * 调 {@code stopUsingItem} 把 flag 清掉，之后客户端 {@code isUsingItem()} 恒为 false——
 * 射线枪的光束、音效、充能消耗（都在 onUseTick / inventoryTick 里）于是完全不发生。
 * <p>
 * 这里用独立的同步字段接管客户端的"使用中"状态，不再依赖 vanilla 那套。
 * <p>
 * 客户端侧的 tick 计数放在 {@link TransientStateContainer} 里，不往实体基类加字段。
 */
public interface GolemUseItemImpl extends TransientStateContainer.View {

	String KEY_USE_TICK = "golem_use_tick";
	String KEY_WAS_USING = "golem_was_using";

	/** 0 = 未使用，1 = 主手，2 = 副手。 */
	default byte getUsingHandFlag() {
		return asGolem().entityData().get(AbstractGolemEntity.DATA_USING_HAND);
	}

	default void setUsingHandFlag(byte flag) {
		asGolem().entityData().set(AbstractGolemEntity.DATA_USING_HAND, flag);
	}

	/** 服务端：把"是否在使用、用哪只手"推给客户端。 */
	default void syncUsingHand() {
		AbstractGolemEntity<?, ?> golem = asGolem();
		byte want = golem.isUsingItem()
				? (byte) (golem.getUsedItemHand() == InteractionHand.OFF_HAND ? 2 : 1)
				: 0;
		if (getUsingHandFlag() != want) {
			setUsingHandFlag(want);
		}
	}

	/** 客户端侧"已使用 tick 数"，独立于 vanilla 的 {@code useItemRemaining}（后者会被 stopUsingItem 清零）。 */
	default int getClientUseTick() {
		Integer value = getTransientState(KEY_USE_TICK);
		return value == null ? 0 : value;
	}

	default void setClientUseTick(int value) {
		setTransientState(KEY_USE_TICK, value);
	}

	default boolean getClientWasUsing() {
		Boolean value = getTransientState(KEY_WAS_USING);
		return value != null && value;
	}

	default void setClientWasUsing(boolean value) {
		setTransientState(KEY_WAS_USING, value ? Boolean.TRUE : null);
	}

	/**
	 * 客户端：原版不会为傀儡派发 {@code Item#onUseTick} / {@code Item#inventoryTick}（装备槽不在背包里，
	 * 且 useItem 字段不同步），这里按同步来的使用状态手动推进。
	 * <p>
	 * {@code timeUsing} 用 {@code getUseDuration() - clientUseTick} 还原成原版语义：物品内部普遍按
	 * {@code i = getUseDuration() - timeUsing} 算已用时长，从 0 开始递增。
	 */
	default void tickClientUseItem() {
		AbstractGolemEntity<?, ?> golem = asGolem();
		boolean using = golem.isUsingItem();
		ItemStack stack = golem.getUseItem();
		if (using && !stack.isEmpty()) {
			if (!getClientWasUsing()) {
				setClientUseTick(0);
			}
			int elapsed = getClientUseTick();
			setClientUseTick(elapsed + 1);
			stack.getItem().onUseTick(golem.level(), golem, stack, stack.getUseDuration() - elapsed);
			// 玩家手持物品时背包会 tick 它，但 AC 的射线枪 / 方铅护手除外——
			// 它们的 {@code UseTime} 必须只有<b>一个</b>写入者，否则两处各写一次，
			// {@code PrevUseTime} 与 {@code UseTime} 会被反复拉开：
			//   · 射线枪：{@code pulseAlpha = useAmount * (...)} 一直非零 → 枪身发光在不使用时也闪；
			//   · 方铅护手：手指动画幅度 {@code openAmount = UseTime/5} 乱跳 → 模型抖动。
			// 射线枪由 {@code ACDispatch#settleRenderedUseTime} 独占推进（每 tick 对齐两值）；
			// 方铅护手由服务端 {@code ACDispatch#tickGauntletUseTime} 独占推进，客户端只读。
			if (!isAcUseTimeItem(stack)) {
				stack.getItem().inventoryTick(stack, golem.level(), golem, 0, true);
			}
		} else if (getClientWasUsing()) {
			// 服务端停止使用：触发 releaseUsing，客户端算出的充能消耗才会随 UpdateItemTagMessage 发回服务端。
			ItemStack last = golem.getItemInHand(golem.getUsedItemHand());
			if (!last.isEmpty()) {
				last.getItem().releaseUsing(last, golem.level(), golem, 0);
			}
		}
		setClientWasUsing(using);
		GolemUseItemHook.POST_TICK.accept(golem);
	}
	/** 这些物品的 {@code UseTime} 有专门的单写入者，不能再走 {@code inventoryTick}。 */
	private static boolean isAcUseTimeItem(ItemStack stack) {
		return stack.getItem() instanceof com.github.alexmodguy.alexscaves.server.item.GalenaGauntletItem
				|| stack.getItem() instanceof com.github.alexmodguy.alexscaves.server.item.RaygunItem;
	}

}
