package dev.xkmc.modulargolems.util;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.registrate.GolemMiscs;
import net.minecraft.sounds.SoundEvent;

public class SkillSound {

	public static void playCharge(AbstractGolemEntity<?, ?> golem, SoundEvent normal, float volume, float pitch) {
		golem.playSound(normal, volume, pitch);
		if (EsterEggUtil.isKobe(golem)) {
			golem.playSound(GolemMiscs.MAN.get(), volume, pitch);
		}
	}

	/** 只播技能音效，不带彩蛋。用于每次命中都播、而彩蛋每次触发只播一次的技能。 */
	public static void playChargeOnly(AbstractGolemEntity<?, ?> golem, SoundEvent normal, float volume, float pitch) {
		golem.playSound(normal, volume, pitch);
	}

	public static void playQuake(AbstractGolemEntity<?, ?> golem, SoundEvent normal, float volume, float pitch) {
		golem.playSound(normal, volume, pitch);
		if (EsterEggUtil.isKobe(golem)) {
			golem.playSound(GolemMiscs.MANBA.get(), volume, pitch);
		}
	}

	/** 只播技能音效，不带彩蛋。用于每次命中或每 tick 都播、而彩蛋每次触发只播一次的技能。 */
	public static void playQuakeOnly(AbstractGolemEntity<?, ?> golem, SoundEvent normal, float volume, float pitch) {
		golem.playSound(normal, volume, pitch);
	}

	/**
	 * 消耗材料类交互（玩家右键喂料 / 傀儡自动消耗）共用的音效：固定音量，音调在 ±20% 内随机，
	 * 避免每次完全一致。各联动技能统一走这里，不要各写一份。
	 */
	public static void playConsume(AbstractGolemEntity<?, ?> golem, SoundEvent normal) {
		float pitch = 1 + (golem.getRandom().nextFloat() - golem.getRandom().nextFloat()) * 0.2F;
		golem.playSound(normal, 1, pitch);
	}

	/** 只播彩蛋，用于技能已播过自身音效、只想再叠一层梗的场景。 */
	public static void playChargeEasterEgg(AbstractGolemEntity<?, ?> golem, float volume, float pitch) {
		if (EsterEggUtil.isKobe(golem)) {
			golem.playSound(GolemMiscs.MAN.get(), volume, pitch);
		}
	}

	/** 跳跃类技能共用的彩蛋。 */
	public static void playQuakeEasterEgg(AbstractGolemEntity<?, ?> golem, float volume, float pitch) {
		if (EsterEggUtil.isKobe(golem)) {
			golem.playSound(GolemMiscs.MANBA.get(), volume, pitch);
		}
	}

}