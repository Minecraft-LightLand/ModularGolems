package dev.xkmc.modulargolems.content.entity.dog;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class RideCooldownIndicator {

	public record Entry(Supplier<MutableComponent> readyText, IntSupplier cooldown,
						IntFunction<MutableComponent> cooldownText) {
	}

	public record Update(MutableComponent text, boolean ping) {
	}

	private final Entry entry;
	private int prev = -2;

	public RideCooldownIndicator(Entry entry) {
		this.entry = entry;
	}

	public Update tick() {
		int cd = entry.cooldown().getAsInt();
		if (cd < 0) {
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
