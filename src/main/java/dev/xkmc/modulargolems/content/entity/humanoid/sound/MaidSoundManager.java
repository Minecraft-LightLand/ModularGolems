package dev.xkmc.modulargolems.content.entity.humanoid.sound;

public class MaidSoundManager extends SoundManager {

	public static final MaidSoundManager INS = new MaidSoundManager();

	public float getSoundVolume() {
		return 1;
	}

	public float getVoicePitch() {
		return 1;
	}

	/*
	@Nullable
	public SoundEvent getAmbientSound() {
		return InitSounds.MAID_IDLE.get();

	}

	public SoundEvent getHurtSound(DamageSource source) {
		if (source.is(DamageTypeTags.IS_FIRE)) {
			return InitSounds.MAID_HURT_FIRE.get();
		}
		return InitSounds.MAID_HURT.get();
	}

	public SoundEvent getDeathSound() {
		return InitSounds.MAID_DEATH.get();
	}

	public boolean playSound(HumanoidGolemEntity e, SoundEvent soundEvent, float volume, float pitch) {
		if (soundEvent.getLocation().getPath().startsWith("maid") && !e.level().isClientSide) {
			NetworkHandler.sendToNearby(e, new PlayMaidSoundPackage(soundEvent.getLocation(), e.getSoundPackId(), e.getId()), 16);
			return true;
		}
		return false;
	}

	 */

}
