package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.content.modifier.special.BaseChargeGoal;

import dev.xkmc.modulargolems.content.modifier.special.ICooldownSkill;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataCompatRegistry;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import dev.xkmc.modulargolems.util.GolemCombatHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class ScyllaSpinChargeGoal extends BaseChargeGoal {

	public static final String KEY_ACTIVE = "ScyllaSpinCharging";
	private static final String KEY_TIME = "ScyllaSpinChargeTime";

	private static final int MAX_TICKS = 60, COOLDOWN = 160;
	/** 冲锋期间每 tick 累加的水平旋转角度（度），即整体转速。 */
	private static final float SPIN_SPEED = 15.0F;
	private static final double RANGE = 16;

	private final AbstractGolemEntity<?, ?> golem;
	private Vec3 start, dir;
	private double total;
	private int ticks;
	private boolean blocked;
	/**
	 * 冲锋期间累加的视觉水平旋转角。只改渲染朝向（yBodyRot），不影响冲锋方向——
	 * 位移方向始终走独立的 {@link #dir}，所以傀儡边转边沿直线冲。
	 */
	private float spinYaw;

	public ScyllaSpinChargeGoal(AbstractGolemEntity<?, ?> golem, int lv, ChargeHelper.Modifier mod) {
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
		// 同 IgnisChargeGoal#start：canUse 与 start 之间目标可能已死
		if (target == null) {
			this.blocked = true;
			return;
		}
		this.ticks = 0;
		this.blocked = false;
		this.spinYaw = 0;
		this.golem.getNavigation().stop();
		this.golem.setTransientFlag(KEY_ACTIVE, true);
		EarthquakeHelper.setBusy(this.golem, true);
		markUsed(golem);
		if (startBackstep(golem, target)) return;
		begin(target);
	}

	private void begin(LivingEntity target) {
		this.start = golem.position();
		this.dir = new Vec3(target.getX() - golem.getX(), 0, target.getZ() - golem.getZ()).normalize();
		this.total = golem.distanceTo(target) + 2;
		GolemCataProxy.scyllaStormParticles(golem);
		GolemCataProxy.scyllaSpinSound(golem);	}

	@Override
	public boolean canContinueToUse() {
		return !blocked && ticks < MAX_TICKS && golem.position().distanceToSqr(start) < total * total;
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
		if (this.ticks % 2 == 0) {
			GolemCataProxy.scyllaStormParticles(golem);
		}
		if (this.ticks % 10 == 0) {
			GolemCataProxy.scyllaSpin(golem);
			GolemCataProxy.scyllaSpinSound(golem);
			GolemCataProxy.screenShake(golem, 20.0F);
		}
		if (!ChargeHelper.moveOneTick(golem, dir)) {
			this.blocked = true;
			return;
		}
		// moveOneTick 每 tick 把 yBodyRot 设回冲锋方向，所以旋转要加在它之后，
		// 且必须「基准方向 + 累计角」，不能把累计角再叠加到已被重置的 yBodyRot 上（会越转越大、视觉乱跳）。
		// 位移方向始终是独立的 dir，所以傀儡边转边沿直线冲。
		float prevSpin = this.spinYaw;
		this.spinYaw = (this.spinYaw + SPIN_SPEED) % 360.0F;
		float baseYaw = (float) (Mth.atan2(-dir.x, dir.z) * Mth.RAD_TO_DEG);
		// 渲染读的是 yBodyRot 与 yBodyRotO 的插值；上一帧值必须保留上一帧的累计角，否则插值恒为 0、看不到转动。
		golem.yBodyRotO = baseYaw + prevSpin;
		golem.yBodyRot = baseYaw + this.spinYaw;
		// setYRot 也同步，避免其它渲染路径（实体朝向）盖掉旋转
		golem.setYRot(golem.yBodyRot);
		golem.yRotO = golem.yBodyRotO;
		golem.yHeadRot = golem.yBodyRot;
		golem.yHeadRotO = golem.yBodyRotO;
		float atk = (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE);
		AABB aabb = golem.getBoundingBox().inflate(0.5);
		for (var e : golem.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
			if (!GolemTargetUtil.canTarget(golem, e)) continue;
			float dmg = atk + Math.min(atk, e.getMaxHealth() * 0.05f);
			float bonus = 1F;
			if (golem.getItemBySlot(EquipmentSlot.CHEST).is(CataCompatRegistry.MONSTROSITY_CHESTPLATE.get()))
				bonus += MGConfig.COMMON.monstrosityChargeBonus.get().floatValue();
			GolemCombatHelper.meleeHit(golem, e, golem.damageSources().mobAttack(golem), dmg * bonus);
		}
	}

	private LivingEntity findTarget() {
		return ChargeHelper.findChargeTarget(golem, RANGE);
	}

}
