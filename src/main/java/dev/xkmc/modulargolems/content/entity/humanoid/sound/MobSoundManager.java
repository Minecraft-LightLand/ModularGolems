package dev.xkmc.modulargolems.content.entity.humanoid.sound;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class MobSoundManager extends SoundManager {

	public static final Map<EntityType<?>, MobSoundManager> MAP = new LinkedHashMap<>();

	static {
		MAP.put(EntityType.ZOMBIE, new MobSoundManager(SoundEvents.ZOMBIE_AMBIENT, SoundEvents.ZOMBIE_HURT, SoundEvents.ZOMBIE_DEATH));
		MAP.put(EntityType.HUSK, new MobSoundManager(SoundEvents.HUSK_AMBIENT, SoundEvents.HUSK_HURT, SoundEvents.HUSK_DEATH));
		MAP.put(EntityType.DROWNED, new MobSoundManager(SoundEvents.DROWNED_AMBIENT, SoundEvents.DROWNED_HURT, SoundEvents.DROWNED_DEATH));
		MAP.put(EntityType.SKELETON, new MobSoundManager(SoundEvents.SKELETON_AMBIENT, SoundEvents.SKELETON_HURT, SoundEvents.SKELETON_DEATH));
		MAP.put(EntityType.WITHER_SKELETON, new MobSoundManager(SoundEvents.WITHER_SKELETON_AMBIENT, SoundEvents.WITHER_SKELETON_HURT, SoundEvents.WITHER_SKELETON_DEATH));
		MAP.put(EntityType.STRAY, new MobSoundManager(SoundEvents.STRAY_AMBIENT, SoundEvents.STRAY_HURT, SoundEvents.STRAY_DEATH));
		MAP.put(EntityType.PIGLIN, new MobSoundManager(SoundEvents.PIGLIN_AMBIENT, SoundEvents.PIGLIN_HURT, SoundEvents.PIGLIN_DEATH));
		MAP.put(EntityType.PIGLIN_BRUTE, new MobSoundManager(SoundEvents.PIGLIN_BRUTE_AMBIENT, SoundEvents.PIGLIN_BRUTE_HURT, SoundEvents.PIGLIN_BRUTE_DEATH));
		MAP.put(EntityType.ZOMBIFIED_PIGLIN, new MobSoundManager(SoundEvents.ZOMBIFIED_PIGLIN_AMBIENT, SoundEvents.ZOMBIFIED_PIGLIN_HURT, SoundEvents.ZOMBIFIED_PIGLIN_DEATH));
	}

	private final SoundEvent ambient, hurt, death;

	public MobSoundManager(SoundEvent ambient, SoundEvent hurt, SoundEvent death) {
		this.ambient = ambient;
		this.hurt = hurt;
		this.death = death;
	}

	@Override
	public @Nullable SoundEvent getAmbientSound() {
		return ambient;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return hurt;
	}

	@Override
	public SoundEvent getDeathSound() {
		return death;
	}

	@Override
	public float getSoundVolume() {
		return 1;
	}

	@Override
	public float getVoicePitch() {
		return 1;
	}

}
