package dev.xkmc.modulargolems.compat.materials.eeeab.guardian;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.util.GolemUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

/**
 * Nameless Guardian - Attack 8: Leap + SmashDown Earthquake
 * Original: {@code GuardianLeapGoal} (LEAP_ANIMATION 105) -> {@code SMASH_DOWN_ANIMATION} (21)
 * Uses jump + shockAttack via {@code ShockWaveUtils} and {@code EntityFallingBlock} visuals.
 * Decompiled: com/eeeab/eeeabsmobs/sever/entity/ai/goal/animate/GuardianLeapGoal.java:35-81
 * and EntityNamelessGuardian.java:418-430 (SMASH_DOWN shockAttack)
 * <p>
 * This modifier follows the {@link EarthquakeHelper.Modifier} pattern like
 * {@code IgnisJumpModifier}, {@code SlamModifier}, {@code MaledictusEarthquakeModifier}.
 */
public class GuardianLeapModifier extends GolemModifier implements EarthquakeHelper.Modifier {

	public GuardianLeapModifier() {
		super(StatFilterType.ATTACK, 3);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> cons) {
		cons.accept(GolemFlags.EARTH_QUAKE);
	}

	@Override
	public void handleEvent(AbstractGolemEntity<?, ?> golem, int value, byte event) {
		if (event == EarthquakeHelper.FLAG) {
			EarthquakeHelper.makeParticles(golem, 0, 0);
		}
	}

	@Override
	public double getEarthquakeRangeSqr(AbstractGolemEntity<?, ?> golem, LivingEntity target, int lv) {
		// original leap triggers at 16..24 blocks (GuardianAIGoal canLeap)
		return 25 * 25;
	}

	@Override
	public void performJump(AbstractGolemEntity<?, ?> golem, int lv) {
		LivingEntity target = golem.getTarget();
		if (target == null) {
			// no target: simple upward jump
			float radians = (float) Math.toRadians(golem.getYRot() + 90.0F);
			golem.setDeltaMovement(3.0 * Math.cos(radians), 1.0, 3.0 * Math.sin(radians));
			golem.hasImpulse = true;
			return;
		}
		// replicate GuardianLeapGoal.findTargetPoint
		Vec3 vec = findTargetPoint(golem, target);
		double yMotion = 1.0 + Mth.clamp(vec.y * 0.055, 0.0, 12.0);
		golem.setDeltaMovement(vec.x * 0.155, yMotion, vec.z * 0.155);
		golem.hasImpulse = true;
	}

	@Override
	public void performEarthQuake(AbstractGolemEntity<?, ?> golem, int lv) {
		golem.playSound(SoundEvents.GENERIC_EXPLODE, 1.5F, 1.0F + golem.getRandom().nextFloat() * 0.1F);
		// original SMASH_DOWN shockAttack: distance 1..2, maxFalling 1.5, spread 2.0, baseDamage 0.5, dmgMult 0.6/0.8
		// for golem: simple AoE within 7 blocks, damage scaled by lv
		float baseDamage = (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE);
		// level scaling: +20% per level beyond 1
		float damage = baseDamage * (0.8F + 0.2F * lv);
		// apply health-percentage bonus similar to guardianHurtTarget's getDamageAmountByTargetHealthPct
		// simplified via GolemUtils.adjustedDamage with small health bonus
		for (LivingEntity entity : golem.level().getEntitiesOfClass(LivingEntity.class, golem.getBoundingBox().inflate(7.0, 3.0, 7.0))) {
			if (golem.isAlliedTo(entity) || entity == golem) continue;
			float finalDamage = GolemUtils.adjustedDamage(damage, entity.getMaxHealth() * 0.02F);
			boolean flag = entity.hurt(golem.damageSources().mobAttack(golem), finalDamage);
			if (flag) {
				EarthquakeHelper.launch(golem, entity, 0.8F);
				// slight upward lift for entities above golem (mirrors original shock y 0.06)
				if (entity.getY() > golem.getY() + 3.0) {
					entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0.06, 0));
				}
			}
		}
		// particles handled via handleEvent FLAG broadcast by JumpAttackHelper
		golem.level().broadcastEntityEvent(golem, EarthquakeHelper.FLAG);
	}

	@Override
	public int getCoolDown(AbstractGolemEntity<?, ?> golem, int lv) {
		// original: MAX_LEAP_TICK 500, MIN 300 -> use 300 for golem, reduced by lv
		int base = 300 - 40 * (lv - 1);
		return Math.max(120, base);
	}

	/**
	 * Copied from GuardianLeapGoal.findTargetPoint
	 * See: /tmp/eeeab_out/com/eeeab/eeeabsmobs/sever/entity/ai/goal/animate/GuardianLeapGoal.java:72-80
	 */
	public static Vec3 findTargetPoint(LivingEntity attacker, LivingEntity target) {
		Vec3 vec3 = target.position();
		float width = Math.min(target.getBbWidth(), 1.5F);
		RandomSource random = attacker.getRandom();
		double radians = Math.toRadians(attacker.getYRot() + 90.0F);
		double randomXOffset = -(1.5 + width) * Math.cos(radians) + (random.nextFloat() - 0.5) * width * 2.0;
		double randomZOffset = -(1.5 + width) * Math.sin(radians) + (random.nextFloat() - 0.5) * width * 2.0;
		return new Vec3(vec3.x - attacker.getX() + randomXOffset, vec3.y - attacker.getY(), vec3.z - attacker.getZ() + randomZOffset);
	}

}
