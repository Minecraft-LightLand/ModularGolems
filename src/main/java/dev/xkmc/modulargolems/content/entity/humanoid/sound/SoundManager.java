package dev.xkmc.modulargolems.content.entity.humanoid.sound;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;

import javax.annotation.Nullable;

public class SoundManager {

	public static final SoundManager INS = new SoundManager();

	@Nullable
	public SoundEvent getAmbientSound() {
		return null;
	}

	public SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.IRON_GOLEM_HURT;
	}

	public SoundEvent getDeathSound() {

		return SoundEvents.IRON_GOLEM_DEATH;
	}

	public float getSoundVolume() {
		return 0.6f;
	}

	public float getVoicePitch() {
		return 1.25f;
	}

	public boolean playSound(HumanoidGolemEntity e, SoundEvent soundEvent, float volume, float pitch) {
		return false;
	}


}
