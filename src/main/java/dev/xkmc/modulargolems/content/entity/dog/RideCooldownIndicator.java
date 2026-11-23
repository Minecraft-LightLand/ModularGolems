package dev.xkmc.modulargolems.content.entity.dog;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.function.IntSupplier;

/**
 * 骑乘技能的"就绪提示音"。冷却的具体数值改到左侧 HUD（RideSkillHud）显示，
 * 这里只负责在技能从冷却回到就绪的那一刻播一次提示音。
 */
public class RideCooldownIndicator {

	private final IntSupplier cooldown;
	private int prev = -2;

	public RideCooldownIndicator(IntSupplier cooldown) {
		this.cooldown = cooldown;
	}

	/** @return 本 tick 是否刚就绪（提示音只在这时播一次） */
	public boolean tick() {
		int cd = cooldown.getAsInt();
		if (cd < 0) {
			this.prev = -2;
			return false;
		}
		boolean ready = cd == 0 && this.prev > 0;
		this.prev = cd;
		return ready;
	}

	public static void render(Player player, List<RideCooldownIndicator> indicators, SoundEvent pingSound) {
		boolean ping = false;
		for (var indicator : indicators) {
			if (indicator.tick()) ping = true;
		}
		if (ping) player.playSound(pingSound, 1.0F, 1.0F);
	}

}
