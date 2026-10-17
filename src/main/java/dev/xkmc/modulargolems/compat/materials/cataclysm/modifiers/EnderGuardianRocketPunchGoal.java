package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.content.modifier.special.BaseChargeGoal;

import dev.xkmc.modulargolems.content.modifier.special.ICooldownSkill;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataCompatRegistry;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import dev.xkmc.modulargolems.util.GolemCombatHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import dev.xkmc.modulargolems.util.GolemTargetUtil;

public class EnderGuardianRocketPunchGoal extends BaseChargeGoal {

	public static final String KEY_ACTIVE = "EnderGuardianRocketPunching";
	private static final String KEY_TIME = "EnderGuardianRocketPunchTime";
	public static final int STUN_TICKS = 60;

	private static final int MAX_TICKS = 40, COOLDOWN = 160;
	private static final double RANGE = 16;

	private final AbstractGolemEntity<?, ?> golem;
	private Vec3 start, dir;
	private double total;
	private int ticks;
	private boolean blocked;

	public EnderGuardianRocketPunchGoal(AbstractGolemEntity<?, ?> golem, int lv, ChargeHelper.Modifier mod) {
		super(mod, lv);
		this.golem = golem;
		setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	public static boolean isCharging(AbstractGolemEntity<?, ?> golem) {
		return golem.isTransientFlag(KEY_ACTIVE);
	}

	@Override
	public boolean canUse() {
		if (golem.isRidingGolemCombo() || !golem.getPassengers().isEmpty() || golem.isInRangedMode()) return false;
		// EarthquakeHelper 的 busy 标志是所有冲锋与跳跃技能共用的全局锁；
		// 与其中之一并行会让双方争抢本技能要重新站位的那个位置
		if (EarthquakeHelper.isBusy(golem)) return false;
		long time = golem.level().getGameTime();
		if (onCooldown(golem)) return false;
		LivingEntity target = findTarget();
		if (target == null || !target.isAlive() || !golem.canAttack(target)) return false;
		if (!ChargeHelper.isTargetReachable(golem, target)) return false;
		double dist = golem.distanceTo(target);
		return dist <= RANGE;
	}

	@Override
	public void start() {
		LivingEntity target = findTarget();
		this.ticks = 0;
		this.blocked = false;
		this.golem.getNavigation().stop();
		this.golem.setTransientFlag(KEY_ACTIVE, true);
		markUsed(golem);
		if (startBackstep(golem, target)) return;
		begin(target);
	}

	private void begin(LivingEntity target) {
		this.start = golem.position();
		this.dir = new Vec3(target.getX() - golem.getX(), 0, target.getZ() - golem.getZ()).normalize();
		this.total = golem.distanceTo(target) + 2;
		GolemCataProxy.guardianExplosionSound(golem);
		ChargeHelper.playChargeEasterEgg(golem);
	}

	@Override
	public boolean canContinueToUse() {
		if (!golem.getPassengers().isEmpty() || golem.isRidingGolemCombo()) return false;
		return !blocked && ticks < MAX_TICKS && golem.position().distanceToSqr(start) < total * total;
	}

	@Override
	public void stop() {
		finishCharge(this.golem, KEY_ACTIVE);
		if (this.ticks > 5) {
			LivingEntity target = golem.getTarget();
			GolemCataProxy.rocketPunch(golem, target);
		}
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	public static void fireBulletFan(AbstractGolemEntity<?, ?> golem) {
		LivingEntity target = golem.getTarget();
		if (target == null || !target.isAlive()) return;
		GolemCataProxy.shootEnderBullet(golem, target);
	}

	@Override
	public void tick() {
		if (tickBackstep()) return;
		if (pendingChargeTarget != null) {
			// 后撤步刚落地：重新占锁并开始冲锋
			LivingEntity t = pendingChargeTarget;
			pendingChargeTarget = null;
			this.ticks = 0;
			EarthquakeHelper.setBusy(golem, true);
			golem.setTransientFlag(KEY_ACTIVE, true);
			begin(t);
			return;
		}
		this.ticks++;
		GolemCataProxy.gravityPull(golem);
		if (this.ticks % 20 == 1) {
			fireBulletFan(golem);
		}
		if (!ChargeHelper.moveOneTick(golem, dir)) {
			this.blocked = true;
			return;
		}
		float atk = (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE);
		AABB aabb = golem.getBoundingBox().inflate(0.5);
		for (var e : golem.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
			if (!GolemTargetUtil.canTarget(golem, e)) continue;
			float dmg = atk * 1.1f + Math.min(atk * 1.1f, e.getMaxHealth() * 0.05f);
			float bonus = 1F;
			if (golem.getItemBySlot(EquipmentSlot.CHEST).is(CataCompatRegistry.MONSTROSITY_CHESTPLATE.get()))
				bonus += MGConfig.COMMON.monstrosityChargeBonus.get().floatValue();
			GolemCombatHelper.meleeHit(golem, e, golem.damageSources().mobAttack(golem), dmg * bonus);
			GolemCataProxy.inflictStun(golem, e, STUN_TICKS);
		}
	}

	private LivingEntity findTarget() {
		return ChargeHelper.findChargeTarget(golem, RANGE);
	}

}
