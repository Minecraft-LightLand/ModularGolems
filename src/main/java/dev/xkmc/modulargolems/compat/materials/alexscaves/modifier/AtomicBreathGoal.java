package dev.xkmc.modulargolems.compat.materials.alexscaves.modifier;

import com.github.alexmodguy.alexscaves.server.misc.ACDamageTypes;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;

/**
 * Fires the atomic breath beam for a random duration, hitting everything in radius along the beam.
 * Mirrors {@code TremorzillaEntity.tickBreath()}, minus block destruction.
 * <p>
 * Built on {@link BaseRangedAttackGoal} like the other beam skills, so it shares
 * {@code specialAttackCoolDown} with the melee goals and declares no goal flags, meaning it cannot
 * be preempted by the movement goals.
 */
public class AtomicBreathGoal extends BaseRangedAttackGoal {

	private static final float BEAM_SOUND_VOLUME = 2.0F;

	private int breathTicks;
	private int breathDuration;

	public AtomicBreathGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(MGConfig.COMMON.atomicBeamCooldown.get(), 0,
				(int) AtomicBreathModifier.range(), golem, lv);
	}

	@Override
	public boolean canUse() {
		if (golem.isRidingGolemCombo() || !golem.getPassengers().isEmpty()) return false;
		if (golem.isBreathAttack()) return false;
		return super.canUse();
	}

	@Override
	public boolean canContinueToUse() {
		if (golem.isBreathAttack()) return true;
		return super.canContinueToUse();
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void stop() {
		if (golem.isBreathAttack()) {
			golem.setBreathAttack(false);
			golem.playSound(ACSoundRegistry.TREMORZILLA_BEAM_END.get(), BEAM_SOUND_VOLUME, 1.0F);
		}
		super.stop();
	}

	@Override
	public void tick() {
		if (golem.isBreathAttack()) {
			this.breathTicks++;
			if (this.breathTicks >= this.breathDuration) {
				golem.setBreathAttack(false);
				golem.playSound(ACSoundRegistry.TREMORZILLA_BEAM_END.get(), BEAM_SOUND_VOLUME, 1.0F);
			} else {
				LivingEntity target = golem.getTarget();
				if (target != null) golem.getLookControl().setLookAt(target, 30, 90);
				if (this.breathTicks % AtomicBreathModifier.DAMAGE_INTERVAL == 0) {
					damageAlongBeam();
				}
			}
		}
		super.tick();
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		if (golem.isBreathAttack()) return false;
		this.breathTicks = 0;
		this.breathDuration = MGConfig.COMMON.atomicBeamDuration.get();
		golem.getNavigation().stop();
		golem.setBreathAttack(true);
		golem.playSound(ACSoundRegistry.TREMORZILLA_BEAM_START.get(), BEAM_SOUND_VOLUME, 1.0F);
		return true;
	}

	private void damageAlongBeam() {
		Vec3 from = AtomicBreathModifier.shootFrom(golem);
		Vec3 to = AtomicBreathModifier.beamEnd(golem);
		Vec3 dir = to.subtract(from);
		float len = (float) dir.length();
		if (len < 1) return;
		dir = dir.scale(1 / len);
		for (float walked = 0; walked < len; walked += AtomicBreathModifier.WALK_STEP) {
			hurtAround(from.add(dir.scale(walked + AtomicBreathModifier.STEP)));
		}
		hurtAround(to);
	}

	private void hurtAround(Vec3 center) {
		double radius = AtomicBreathModifier.radius();
		var aabb = new AABB(center.subtract(radius, radius, radius), center.add(radius, radius, radius));
		double damage = AtomicBreathModifier.damage(lv);
		double force = AtomicBreathModifier.force();
		var source = ACDamageTypes.causeTremorzillaBeamDamage(golem.level().registryAccess(), golem);
		for (LivingEntity living : golem.level().getEntitiesOfClass(LivingEntity.class, aabb,
				e -> e instanceof Player player ? !player.isCreative() && !player.isSpectator() : true)) {
			if (living == golem || !GolemTargetUtil.canTarget(golem, living)) continue;
			if (living.distanceToSqr(center) > radius * radius) continue;
			Vec3 eye = living.position().add(0, living.getBbHeight() * 0.5, 0);
			if (!AtomicBreathModifier.canSee(golem, center, eye)) continue;
			if (!living.hurt(source, (float) damage)) continue;
			living.knockback(force, golem.getX() - living.getX(), golem.getZ() - living.getZ());
			living.addEffect(new MobEffectInstance(ACEffectRegistry.IRRADIATED.get(),
					AtomicBreathModifier.IRRADIATED_DURATION, AtomicBreathModifier.IRRADIATED_AMPLIFIER));
		}
	}

	@Override
	protected BackstepHelper backstep() {
		return new BackstepHelper(golem, BackstepHelper.DEFAULT_DISTANCE, BackstepHelper.DEFAULT_HEIGHT);
	}


	@Override
	protected double getLeapRange() {
		return BackstepHelper.DEFAULT_LEAP_RANGE;
	}
}
