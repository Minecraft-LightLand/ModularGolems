package dev.xkmc.modulargolems.content.modifier.special;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class SonicAttackGoal extends Goal {

	private static final int WAIT = 200, DELAY = 34;

	private final AbstractGolemEntity<?, ?> warden;
	private final int lv;
	private int attackTime = WAIT;
	private Vec3 targetPos;

	public SonicAttackGoal(AbstractGolemEntity<?, ?> warden, int lv) {
		this.warden = warden;
		this.lv = lv;
	}

	/**
	 * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
	 * method as well.
	 */
	public boolean canUse() {
		LivingEntity livingentity = this.warden.getTarget();
		if (targetPos != null && attackTime <= DELAY)
			return true;
		return livingentity != null &&
				livingentity.isAlive() &&
				this.warden.canAttack(livingentity) &&
				this.warden.distanceToSqr(livingentity) < 256 &&
				(this.warden.getNavigation().isStuck() || this.warden.distanceToSqr(livingentity) > 4);
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void start() {
		this.attackTime = WAIT;
		this.targetPos = null;
	}

	/**
	 * Reset the task's internal state. Called when this task is interrupted by another one
	 */
	public void stop() {
	}

	public boolean requiresUpdateEveryTick() {
		return true;
	}

	/**
	 * Keep ticking a continuous task that has already been started
	 */
	public void tick() {
		--this.attackTime;
		LivingEntity le = warden.getTarget();
		if (le != null) {
			if (attackTime == DELAY) {
				warden.playSound(SoundEvents.WARDEN_SONIC_CHARGE, 3.0F, 1.0F);
			}
			if (attackTime <= DELAY) {
				targetPos = le.getEyePosition();
			}
		}
		if (attackTime <= 0 && targetPos != null) {
			Vec3 src = warden.position().add(0.0D, 1.6F, 0.0D);
			Vec3 dst = targetPos.subtract(src);
			Vec3 dir = dst.normalize();

			for (int i = 1; i < 17; ++i) {
				Vec3 vec33 = src.add(dir.scale(i));
				if (warden.level instanceof ServerLevel level)
					level.sendParticles(ParticleTypes.SONIC_BOOM, vec33.x, vec33.y, vec33.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
			}

			warden.playSound(SoundEvents.WARDEN_SONIC_BOOM, 3.0F, 1.0F);
			var attr = warden.getAttribute(GolemTypes.GOLEM_SWEEP.get());
			List<LivingEntity> target = new ArrayList<>();
			if (attr != null && attr.getValue() > 0) {
				AABB aabb = new AABB(src, src.add(dir.scale(16)));
				for (var e : warden.level.getEntities(warden, aabb)) {
					if (e instanceof LivingEntity x && warden.canAttack(x)) {
						target.add(x);
					}
				}
			}
			if (le != null && !target.contains(le)) {
				target.add(le);
			}
			for (var e : target) {
				e.hurt(warden.level.damageSources().sonicBoom(warden), 10 * lv);
				double d1 = 0.5D * (1.0D - e.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
				double d0 = 2.5D * (1.0D - e.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
				e.push(dir.x() * d0, dir.y() * d1, dir.z() * d0);
			}
			this.attackTime = WAIT;
			this.targetPos = null;
		}
		super.tick();
	}

}

