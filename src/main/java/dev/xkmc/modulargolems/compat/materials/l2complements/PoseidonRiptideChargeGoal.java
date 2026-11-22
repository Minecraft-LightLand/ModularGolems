package dev.xkmc.modulargolems.compat.materials.l2complements;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseChargeGoal;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class PoseidonRiptideChargeGoal extends BaseChargeGoal {

	public static final String KEY_ACTIVE = "PoseidonRiptideCharging";

	private static final int MAX_TICKS = 60;
	private static final double RANGE = 16;

	private final AbstractGolemEntity<?, ?> golem;
	private Vec3 start, dir;
	private double total;
	private int ticks;
	private boolean blocked;

	public PoseidonRiptideChargeGoal(AbstractGolemEntity<?, ?> golem, int lv, ChargeHelper.Modifier mod) {
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
		if (!golem.isInWaterRainOrBubble()) return false;
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
		Vec3 diff = target.getEyePosition().subtract(golem.getEyePosition());
		Vec3 flat = new Vec3(diff.x, 0, diff.z);
		if (diff.y > 2.0 && flat.lengthSqr() > 1e-8) {
			this.dir = diff.normalize();
		} else if (flat.lengthSqr() > 1e-8) {
			this.dir = flat.normalize();
		} else {
			this.dir = diff.y >= 0 ? new Vec3(0, 1, 0) : new Vec3(0, -1, 0);
		}
		this.total = golem.distanceTo(target) + 2;
		golem.playSound(SoundEvents.TRIDENT_RIPTIDE_1.value(), 1.0F, 1.0F);
		ChargeHelper.playChargeEasterEgg(golem);
		golem.setRiptideSpin(true);
		golem.fallDistance = 0;
	}

	@Override
	public void stop() {
		finishCharge(this.golem, KEY_ACTIVE);
		golem.setRiptideSpin(false);
	}

	@Override
	public boolean canContinueToUse() {
		return canContinueCharge(golem, start, total, ticks, MAX_TICKS, blocked);
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
		golem.fallDistance = 0;
		if (!ChargeHelper.moveOneTick(golem, dir)) {
			this.blocked = true;
			return;
		}
		float atk = (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE);
		AABB aabb = golem.getBoundingBox().inflate(0.5);
		for (var e : golem.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
			if (!GolemTargetUtil.canTarget(golem, e)) continue;
			e.hurt(golem.damageSources().mobAttack(golem), atk);
		}
	}

	private LivingEntity findTarget() {
		return ChargeHelper.findChargeTarget(golem, RANGE);
	}

}