package dev.xkmc.modulargolems.compat.materials.eeeab.realm;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.util.GolemUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Consumer;

/**
 * Realm Warden - Jump Smash Attack (Dash + Ground Pound)
 * Original: {@code RWJumpSmashGoal} -> {@code JUMP_SMASH_START_ANIMATION}(35)
 * -> {@code JUMP_SMASH_ANIMATION}(30) / {@code DERIVED_JUMP_SMASH_ANIMATION}(50)
 * Decompiled: com/eeeab/eeeabsmobs/sever/entity/mob/relicron/EntityRealmWarden.java:1855-1917
 * and keyframes: EntityRealmWarden:1170-1112
 * <pre>
 * JUMP_SMASH common (tick7):
 *   pos = getPosOffset(false,2.4f,0,0) // 2.4 forward
 *   doGroundPoundEffect(pos,1.2,1.2, {55,25,35}) + ShockWaveUtils.doRingShockWave(pos,2.25,-0.015,20)
 *   -> hurt 1.0, knock 0.75
 * DERIVED_JUMP_SMASH (tick25):
 *   pos = getPosOffset(false,2.4f,0,0)
 *   doGroundPoundEffect(pos,1.5,1.5,1.0, null, colors)
 *   -> AABB at pos 0,7.5,14,7.5, hurt bypassArmor 1.0, knock 1.0
 *   + attraction pull 15-24: diff*0.07
 * Dash: targetPosCache = start + direction * min(dist,20) (RWJumpSmashGoal:1881-1886)
 *       speedMultiplier = ModMathUtils.calculateSpeedMultiplier(1.0, max(tickFactor(sqrt(distSqr),distanceCache),0.75),1.0,3.0)
 *       setDeltaMovement(direction * speedMultiplier)
 * </pre>
 * This modifier follows {@link EarthquakeHelper.Modifier} pattern like
 * {@code IgnisJumpModifier}, {@code SlamModifier}, {@code GuardianLeapModifier}.
 * Jump = dash, Quake = ground pound + ring shock.
 */
public class RealmWardenJumpSmashModifier extends GolemModifier implements EarthquakeHelper.Modifier {

	public RealmWardenJumpSmashModifier() {
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
		// original: distanceRange 9..24 (setupAnimationRules: jumpSmashStartRule 9..24)
		// HealthScaledCooldown 380,40,60
		return 24 * 24;
	}

	@Override
	public void performJump(AbstractGolemEntity<?, ?> golem, int lv) {
		LivingEntity target = golem.getTarget();
		if (target == null) {
			Vec3 fwd = golem.getForward().normalize();
			golem.setDeltaMovement(fwd.x * 1.5, golem.getDeltaMovement().y, fwd.z * 1.5);
			golem.hasImpulse = true;
			return;
		}
		Vec3 start = golem.position();
		Vec3 toTarget = target.position().subtract(start);
		double dist = toTarget.length();
		double actualRange = Math.min(dist, 20.0);
		if (actualRange < 1e-6) {
			golem.hasImpulse = true;
			return;
		}
		Vec3 dir = toTarget.normalize();
		// replicate speedMultiplier logic: 1.0 .. 3.0 based on distance
		// use simple linear factor for single impulse
		double distanceFactor = Math.max(actualRange / 20.0, 0.75);
		double speedMultiplier = 1.0 + distanceFactor * 2.0; // 2.5 .. 3.0
		// level slightly increases dash speed
		speedMultiplier *= (1.0 + 0.1 * (lv - 1));
		Vec3 motion = new Vec3(dir.x * speedMultiplier, golem.getDeltaMovement().y, dir.z * speedMultiplier);
		golem.setDeltaMovement(motion);
		golem.hasImpulse = true;
		// face target
		float yaw = (float) (Math.atan2(dir.z, dir.x) * 180.0 / Math.PI) - 90.0F;
		golem.setYRot(yaw);
		golem.yBodyRot = yaw;
	}

	@Override
	public void performEarthQuake(AbstractGolemEntity<?, ?> golem, int lv) {
		// sound mirrors doGroundPoundEffect server branch: SHAKE_GROUND + SHOCK
		golem.playSound(SoundEvents.GENERIC_EXPLODE, 1.2F, 1.0F + golem.getRandom().nextFloat() * 0.1F);
		if (golem.getRandom().nextFloat() < 0.5F) {
			// use vanilla blast as placeholder for REALM_WARDEN_BLAST (requires EEEAB SoundInit)
			// if EEEAB present: SoundInit.REALM_WARDEN_BLAST.get()
			golem.playSound(SoundEvents.GENERIC_EXPLODE, 1.5F, 1.5F);
		}
		// frontal pound position: 2.4 forward, mirrors getPosOffset(false,2.4,0,0)
		Vec3 look = golem.getForward().normalize();
		Vec3 poundPos = golem.position().add(look.scale(2.4)).add(0, 0.1, 0);
		// damage mirrors tick7 ring shock (1.0) + derived tick25 bypassArmor (1.0)
		// for golem combine into one AoE with lv scaling
		float base = (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE);
		float damage = base * (0.9F + 0.15F * lv); // 0.9 .. 1.2 as level 1..3
		// first ring-like damage near poundPos (2.25 radius) handled via same AABB for simplicity
		// derived frontal AABB: makeAABBWithSize(pos,0,7.5,14,7.5) -> width 7.5, height 14, depth 7.5
		double halfW = 7.5 * (0.8 + 0.1 * lv);
		double height = 6.0;
		AABB area = new AABB(
				poundPos.x - halfW, poundPos.y - 1.0, poundPos.z - halfW,
				poundPos.x + halfW, poundPos.y + height, poundPos.z + halfW
		);
		// fallback also hit around golem itself (ring shock 2.25)
		AABB selfArea = golem.getBoundingBox().inflate(3.0, 2.0, 3.0);
		// collect unique targets via combined area
		List<LivingEntity> hit = golem.level().getEntitiesOfClass(LivingEntity.class, area,
				e -> e != golem && !golem.isAlliedTo(e) && e.isAlive());
		// add self-area if not already included (simple inflate)
		for (LivingEntity e : golem.level().getEntitiesOfClass(LivingEntity.class, selfArea,
				x -> x != golem && !golem.isAlliedTo(x) && x.isAlive() && !hit.contains(x))) {
			hit.add(e);
		}
		for (LivingEntity target : hit) {
			// original uses ModDamageSource.bypassArmor for derived, normal for base
			// for golem use mobAttack with adjusted damage + health bonus
			float finalDamage = GolemUtils.adjustedDamage(damage, target.getMaxHealth() * 0.015F);
			// derived version bypasses armor/cooldown - mimic by resetting invuln if lv>=3
			boolean isDerived = lv >= 3;
			if (isDerived) {
				target.invulnerableTime = 0;
			}
			boolean hurt = target.hurt(golem.damageSources().mobAttack(golem), finalDamage);
			if (hurt) {
				// knockback mirrors ModEntityUtils.forceKnockBack 0.75..1.0, direction from poundPos
				double dx = target.getX() - poundPos.x;
				double dz = target.getZ() - poundPos.z;
				double len = Math.sqrt(dx * dx + dz * dz);
				if (len > 1e-4) {
					dx /= len;
					dz /= len;
				} else {
					dx = look.x;
					dz = look.z;
				}
				float knock = isDerived ? 1.0F : 0.75F;
				// use EarthquakeHelper.launch for consistency + extra vertical
				EarthquakeHelper.launch(golem, target, knock);
				// slight extra push from pound pos
				target.push(dx * 0.25 * knock, 0.1, dz * 0.25 * knock);
			}
		}
		golem.level().broadcastEntityEvent(golem, EarthquakeHelper.FLAG);
	}

	@Override
	public int getCoolDown(AbstractGolemEntity<?, ?> golem, int lv) {
		// original HealthScaledCooldown 380,40,60,0.5 true -> ~380 at high health, ~190 at low
		// for golem use 320 base, reduced by lv and health scaling simplified
		int base = 380 - 30 * (lv - 1);
		return Math.max(160, base);
	}

}
