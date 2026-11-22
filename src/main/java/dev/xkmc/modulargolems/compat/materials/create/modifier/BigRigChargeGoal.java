package dev.xkmc.modulargolems.compat.materials.create.modifier;

import com.simibubi.create.AllSoundEvents;
import dev.xkmc.modulargolems.compat.materials.create.CreateCompatRegistry;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseChargeGoal;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import dev.xkmc.modulargolems.util.GolemCombatHelper;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import dev.xkmc.modulargolems.util.SkillSound;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class BigRigChargeGoal extends BaseChargeGoal {

	public static final String KEY_ACTIVE = "WindRushCharging";

	public static final int MAX_TICKS = 60, COOLDOWN = 200;
	public static final double RANGE = 16;

	private final AbstractGolemEntity<?, ?> golem;
	private Vec3 start, dir;
	private double total;
	private int ticks;
	private boolean blocked;

	public BigRigChargeGoal(AbstractGolemEntity<?, ?> golem, int lv, ChargeHelper.Modifier mod) {
		super(mod, lv);
		this.golem = golem;
		setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	public static boolean isCharging(AbstractGolemEntity<?, ?> golem) {
		return golem.meleeGoal != null && golem.meleeGoal.busy;
	}

	private void setBusy(boolean busy) {
		if (golem.meleeGoal != null) golem.meleeGoal.busy = busy;
	}

	@Override
	public boolean canUse() {
		if (golem.isRidingGolemCombo() || !golem.getPassengers().isEmpty() || golem.isInRangedMode()) return false;
		if (golem.meleeGoal != null && golem.meleeGoal.busy) return false;
		if (onCooldown(golem)) return false;
		LivingEntity target = findTarget();
		if (target == null || !target.isAlive() || !golem.canAttack(target)) return false;
		if (!ChargeHelper.isTargetReachable(golem, target)) return false;
		return golem.distanceTo(target) <= RANGE;
	}

	@Override
	public void start() {
		LivingEntity target = findTarget();
		if (target == null) {
			this.blocked = true;
			return;
		}
		this.ticks = 0;
		this.blocked = false;
		this.golem.getNavigation().stop();
		setBusy(true);
		markUsed(golem);
		if (startBackstep(golem, target)) return;
		begin(target);
	}

	private void begin(LivingEntity target) {
		this.start = golem.position();
		this.dir = new Vec3(target.getX() - golem.getX(), 0, target.getZ() - golem.getZ()).normalize();
		this.total = golem.distanceTo(target) + 2;
		ChargeHelper.spawnChargeParticles(golem, dir, ParticleTypes.CLOUD, true);
		SkillSound.playCharge(golem, SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 0.6F);
		SkillSound.playCharge(golem, AllSoundEvents.WHISTLE_TRAIN.getMainEvent(), 1.0F, 1.0F);
	}

	@Override
	public boolean canContinueToUse() {
		return canContinueCharge(golem, start, total, ticks, MAX_TICKS, blocked);
	}

	@Override
	public void stop() {
		finishCharge(this.golem, KEY_ACTIVE);
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		if (tickBackstep()) return;
		if (pendingChargeTarget != null) {
			LivingEntity t = pendingChargeTarget;
			pendingChargeTarget = null;
			this.ticks = 0;
			setBusy(true);
			begin(t);
			return;
		}
		this.ticks++;
		if (this.ticks % 3 == 0) {
			ChargeHelper.spawnChargeParticles(golem, dir, ParticleTypes.CLOUD, true);
		}
		if (!ChargeHelper.moveOneTick(golem, dir)) {
			this.blocked = true;
			return;
		}
		float atk = (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE);
		float size = (float) golem.getAttributeValue(GolemTypes.GOLEM_SIZE.holder());
		float speed = (float) golem.getAttributeValue(Attributes.MOVEMENT_SPEED);
		float dmg = atk * size * speed;
		AABB aabb = golem.getBoundingBox().inflate(0.5);
		for (var e : golem.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
			if (!GolemTargetUtil.canTarget(golem, e)) continue;
			GolemCombatHelper.meleeHit(golem, e, golem.damageSources().mobAttack(golem), dmg);
		}
	}

	private LivingEntity findTarget() {
		return ChargeHelper.findChargeTarget(golem, RANGE);
	}

}
