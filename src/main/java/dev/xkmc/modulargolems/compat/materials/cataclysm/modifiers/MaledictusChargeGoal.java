package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.content.modifier.special.BaseChargeGoal;

import dev.xkmc.modulargolems.content.modifier.special.ICooldownSkill;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataCompatRegistry;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import dev.xkmc.modulargolems.init.GolemParticles;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import dev.xkmc.modulargolems.util.GolemCombatHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class MaledictusChargeGoal extends BaseChargeGoal {

	public static final String KEY_ACTIVE = "MaledictusCharging";
	private static final String KEY_TIME = "MaledictusChargeTime";

	private static final int MAX_TICKS = 60;
	private static final double RANGE = 16;

	/**
	 * 连续冲锋段数上限，tooltip 也会显示。实际段数随生命值变化，见 {@link #maxDashFor}。
	 */
	public static final int MAX_DASH = 3;

	/**
	 * 按生命值比例决定本次连续冲锋段数：满血 1 段，50% 以下 2 段，25% 以下 3 段。
	 * 佩戴咒魂护腿时固定为上限。
	 */
	public static int maxDashFor(AbstractGolemEntity<?, ?> golem) {
		if (golem.getItemBySlot(EquipmentSlot.LEGS).is(CataCompatRegistry.MALEDICTUS_SHINGUARD.get()))
			return MAX_DASH;
		float ratio = golem.getGuardedDataImpl() / golem.getMaxHealth();
		if (ratio <= 0.25F) return MAX_DASH;
		if (ratio <= 0.5F) return 2;
		return 1;
	}

	private final AbstractGolemEntity<?, ?> golem;
	private Vec3 start, dir;
	private double total;
	private int ticks;
	private boolean blocked;
	private int dashDone, maxDash;


	public MaledictusChargeGoal(AbstractGolemEntity<?, ?> golem, int lv, ChargeHelper.Modifier mod) {
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
		this.ticks = 0;
		this.blocked = false;
		this.dashDone = 0;
		this.maxDash = maxDashFor(golem);
		this.golem.getNavigation().stop();
		this.golem.setTransientFlag(KEY_ACTIVE, true);
		EarthquakeHelper.setBusy(this.golem, true);
		markUsed(golem);
		LivingEntity target = findTarget();
		if (target == null) {
			blocked = true;
			return;
		}
		// 目标太近先起后撤步，落地后由 tick 接手开始冲锋
		if (startBackstep(golem, target)) return;
		begin(target);
	}

	private void begin(LivingEntity target) {
		this.ticks = 0;
		this.start = golem.position();
		this.dir = new Vec3(target.getX() - golem.getX(), 0, target.getZ() - golem.getZ()).normalize();
		this.total = golem.distanceTo(target) + 4;
		setFacing();
		ChargeHelper.spawnChargeParticles(golem, dir, ParticleTypes.SOUL);
		GolemCataProxy.summonSoulRing(golem);
		GolemCataProxy.maledictusShortRoar(golem);
		GolemCataProxy.maledictusChargeSound(golem);
		ChargeHelper.playChargeEasterEgg(golem);
	}

	private void setFacing() {
		float yaw = (float) (Mth.atan2(-dir.x, dir.z) * Mth.RAD_TO_DEG);
		golem.setYRot(yaw);
		golem.yBodyRot = yaw;
		golem.yRotO = yaw;
		golem.yBodyRotO = yaw;
		// 只锁身体；头部保持朝目标（含俯仰），不随冲锋方向锁死
		var tgt = golem.getTarget();
		if (tgt != null) {
			golem.getLookControl().setLookAt(tgt, 30.0F, 30.0F);
		} else {
			golem.yHeadRot = yaw;
			golem.yHeadRotO = yaw;
			golem.getLookControl().setLookAt(golem.getX() + dir.x * 4, golem.getEyeY(), golem.getZ() + dir.z * 4, 15, 20);
		}
	}

	@Override
	public boolean canContinueToUse() {
		return dashDone < maxDash && !blocked;
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
			this.dashGap = 0;
			EarthquakeHelper.setBusy(golem, true);
			golem.setTransientFlag(KEY_ACTIVE, true);
			begin(t);
			return;
		}
		if (tickDashGap()) {
			// 段间等待：原地停住不推进，等无敌帧过去再起下一段
			if (this.dashGap == 0 && this.pendingTarget != null) {
				var t = this.pendingTarget;
				this.pendingTarget = null;
				if (t.isAlive() && golem.canAttack(t)) begin(t);
				else { this.dashDone = this.maxDash; this.blocked = true; }
			}
			return;
		}
		this.ticks++;
		GolemCataProxy.summonSoulRing(golem);
		GolemParticles.spawnAfterimage(golem);
		if (this.ticks % 4 == 0) {
			ChargeHelper.spawnChargeParticles(golem, dir, ParticleTypes.SOUL);
		}
		if (!ChargeHelper.moveOneTick(golem, dir)) {
			this.blocked = true;
			return;
		}
		setFacing();
		AABB aabb = golem.getBoundingBox().inflate(0.5);
		for (var e : golem.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
			if (!GolemTargetUtil.canTarget(golem, e)) continue;
			float atk = (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE);
			float dmg = atk * 1.1F + Math.min(atk * 1.1F, e.getMaxHealth() * 0.05f);
			float bonus = 1F;
			if (golem.getItemBySlot(EquipmentSlot.CHEST).is(CataCompatRegistry.MONSTROSITY_CHESTPLATE.get()))
				bonus += MGConfig.COMMON.monstrosityChargeBonus.get().floatValue();
			GolemCombatHelper.meleeHit(golem, e, golem.damageSources().mobAttack(golem), dmg * bonus);
		}
		if (golem.position().distanceToSqr(start) >= total * total || ticks >= MAX_TICKS) {
			nextDash();
		}
	}

	private void nextDash() {
		this.dashDone++;
		if (this.dashDone >= this.maxDash) {
			this.blocked = true;
			return;
		}
		LivingEntity target = findTarget();
		if (target == null || !target.isAlive() || !golem.canAttack(target)) {
			this.dashDone = this.maxDash;
			this.blocked = true;
			return;
		}
		// 先进入段间等待，{@link #tick} 倒计时结束后才真正起下一段
		startDashGap();
		this.pendingTarget = target;
	}

	/** 等待期间暂存下一段的目标。 */
	private LivingEntity pendingTarget;

	private LivingEntity findTarget() {
		return ChargeHelper.findChargeTarget(golem, RANGE);
	}

}
