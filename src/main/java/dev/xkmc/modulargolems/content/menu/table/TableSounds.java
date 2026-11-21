package dev.xkmc.modulargolems.content.menu.table;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

/** 装配台操作音效：只在服务端播放，避免客户端预测重复。 */
public final class TableSounds {

	private TableSounds() {
	}

	public static void play(Player player, SoundEvent sound) {
		if (!player.level().isClientSide) {
			player.level().playSound(null, player.blockPosition(), sound, SoundSource.BLOCKS, 1.0F, 1.0F);
		}
	}

}
