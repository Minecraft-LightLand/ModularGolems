package dev.xkmc.modulargolems.compat.materials.alexscaves.modifier;

import dev.xkmc.modulargolems.content.modifier.special.BaseChargeGoal;

import dev.xkmc.modulargolems.util.GolemCombatHelper;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataCompatRegistry;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.util.SkillSound;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class CandyDashAttackGoal extends BaseChargeGoal {

	public static final String KEY_ACTIVE = "CandyDashCharging";
	private static final String KEY_TIME = "CandyDashChargeTime";

	private final AbstractGolemEntity<?, ?> golem;
	private Vec3 start, dir;
	private double total;
	private int ticks;
	private boolean blocked;

	public CandyDashAttackGoal(AbstractGolemEntity<?, ?> golem, int lv, ChargeHelper.Modifier mod) {
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
		if (!canStartCharge(golem)) return false;
		if (EarthquakeHelper.isBusy(golem)) return false;
		long time = golem.level().getGameTime();
		if (onCooldown(golem)) return false;
		LivingEntity target = findTarget();
		if (target == null || !target.isAlive() || !golem.canAttack(target)) return false;
		if (!ChargeHelper.isTargetReachable(golem, target)) return false;
		double dist = golem.distanceTo(target);
		return dist >= CandyDashModifier.MIN_RANGE && dist <= CandyDashModifier.MAX_RANGE;
	}

	@Override
	public void start() {
		LivingEntity target = findTarget();
		// 同 IgnisChargeAttackGoal#start：canUse 与 start 之间目标可能已死
		if (target == null) {
			this.blocked = true;
			return;
		}
		this.ticks = 0;
		this.blocked = false;
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
		CandyDashModifier.setDashFx(golem, dir);
		SkillSound.playCharge(golem, ACSoundRegistry.CANDICORN_CHARGE_START.get(), 1.0F, 1.0F);
	}

	@Override
	public boolean canContinueToUse() {
		return canContinueCharge(golem, start, total, ticks, CandyDashModifier.MAX_TICKS, blocked);
	}

	@Override
	public void stop() {
		finishCharge(this.golem, KEY_ACTIVE);
		CandyDashModifier.clearDashFx(golem);
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
		if (this.ticks % 12 == 0) {
			SkillSound.playChargeOnly(golem, ACSoundRegistry.CANDICORN_GALLOP.get(), 0.6F, 1.0F);
		}
		if (!ChargeHelper.moveOneTick(golem, dir)) {
			this.blocked = true;
			return;
		}
		float bonus = 1F;
		if (golem.getItemBySlot(EquipmentSlot.CHEST).is(CataCompatRegistry.MONSTROSITY_CHESTPLATE.get()))
			bonus += MGConfig.COMMON.monstrosityChargeBonus.get().floatValue();
		Vec3 focal = CandyDashModifier.focalPoint(golem, dir);
		AABB aabb = golem.getBoundingBox().inflate(3.5D);
		var src = golem.damageSources().mobAttack(golem);
		for (var e : golem.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
			if (!GolemTargetUtil.canTarget(golem, e) || e.isPassengerOfSameVehicle(golem)) continue;
			float dmg = CandyDashModifier.chargeDamage(focal, e);
			if (dmg <= 0) continue;
			{
				boolean chargeHit = GolemCombatHelper.meleeHit(golem, e, src, dmg * bonus);
				if (chargeHit) SkillSound.playChargeHit(golem);
			}
			CandyDashModifier.knockBack(focal, e);
		}
	}

	private LivingEntity findTarget() {
		return ChargeHelper.findChargeTarget(golem, CandyDashModifier.MAX_RANGE);
	}

}
