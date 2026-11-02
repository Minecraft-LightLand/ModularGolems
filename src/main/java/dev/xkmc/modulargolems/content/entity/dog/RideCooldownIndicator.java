package dev.xkmc.modulargolems.content.entity.dog;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 * 骑乘技能的冷却读数，各技能共用一份逻辑，不再各自长一段 HUD 代码。
 * <p>
 * 技能只注册一个 {@link Entry}，倒计时文案、就绪文案和剩余时间来源都由条目自己提供。
 * {@link #render} 把任意多条合并成一行，所以加技能不会改动渲染代码。
 */
public class RideCooldownIndicator {

	/**
	 * @param readyText    冷却结束那一 tick 显示的文案
	 * @param cooldown     剩余秒数，0 表示就绪，负数表示傀儡没这个技能（此时不显示，而不是显示 0）
	 * @param cooldownText 把剩余秒数转成倒计时文案
	 */
	public record Entry(Supplier<MutableComponent> readyText, IntSupplier cooldown,
						IntFunction<MutableComponent> cooldownText) {

	}

	/**
	 * 本 tick 的变化，没有则不发消息。
	 *
	 * @param ping 是否是就绪那一刻，提示音只在这里播一次
	 */
	public record Update(MutableComponent text, boolean ping) {

	}

	private final Entry entry;
	private int prev = -2;

	public RideCooldownIndicator(Entry entry) {
		this.entry = entry;
	}

	/** 推进一 tick。多数 tick 返回 null：只在就绪瞬间和每秒倒计时时才有消息。 */
	public Update tick() {
		int cd = entry.cooldown().getAsInt();
		if (cd < 0) {
			// 技能已被卸下，之后重新装上时才能重新播报
			this.prev = -2;
			return null;
		}
		if (cd == this.prev) return null;
		boolean ready = cd == 0 && this.prev > 0;
		this.prev = cd;
		if (ready) return new Update(entry.readyText().get(), true);
		if (cd > 0) return new Update(entry.cooldownText().apply((cd + 19) / 20), false);
		return null;
	}

	public static void render(Player player, List<RideCooldownIndicator> indicators) {
		MutableComponent line = null;
		boolean ping = false;
		for (var indicator : indicators) {
			var update = indicator.tick();
			if (update == null) continue;
			if (line == null) line = Component.empty();
			else line.append("    ");
			line.append(update.text());
			ping |= update.ping();
		}
		if (line == null) return;
		player.displayClientMessage(line, true);
		if (ping) player.playSound(SoundEvents.NOTE_BLOCK_PLING.value(), 1.0F, 2.0F);
	}

}