package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.util.SkillSound;
import dev.xkmc.modulargolems.content.modifier.special.BaseChargeGoal;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;
import dev.xkmc.modulargolems.content.modifier.special.TeleportBackstepHelper;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import dev.xkmc.modulargolems.util.GolemCombatHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import dev.xkmc.modulargolems.util.GolemTargetUtil;

public class EnderGuardianRocketPunchAttackGoal extends BaseChargeGoal {

	public static final String KEY_ACTIVE = "EnderGuardianRocketPunching";

	private final AbstractGolemEntity<?, ?> golem;
	private Vec3 start, dir;
	private double total;
	private int ticks;
	private int bulletTick;
	private boolean blocked;

	public EnderGuardianRocketPunchAttackGoal(AbstractGolemEntity<?, ?> golem, int lv, ChargeHelper.Modifier mod) {
		super(mod, lv);
		this.golem = golem;
		setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	public static boolean isCharging(AbstractGolemEntity<?, ?> golem) {
		return golem.isTransientFlag(KEY_ACTIVE);
	}

	@Override
	public boolean canUse() {
		return canUseCharge(golem, ChargeHelper.findChargeTarget(golem, EnderGuardianRocketPunchModifier.MAX_RANGE), EnderGuardianRocketPunchModifier.MIN_RANGE, EnderGuardianRocketPunchModifier.MAX_RANGE);
	}

	@Override
	public void start() {
		LivingEntity target = ChargeHelper.findChargeTarget(golem, EnderGuardianRocketPunchModifier.MAX_RANGE);
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
		// 引力冲撞必定召唤一排潜影弹：起手锁定冲锋目标先放一次，之后每 20 tick 再补
		GolemCataProxy.shootEnderBullet(golem, target);
		this.bulletTick = 0;
	}

	@Override
	public boolean canContinueToUse() {
		if (!golem.getPassengers().isEmpty() || golem.isRidingGolemCombo()) return false;
		return canContinueCharge(golem, start, total, ticks, EnderGuardianRocketPunchModifier.MAX_TICKS, blocked);
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
		if (CataArmorHelper.guardianLegs(golem)) {
			// 末地守卫护腿：吸力范围和强度都增加
			GolemCataProxy.gravityPull(golem, 18.0D, 0.14D);
		} else {
			GolemCataProxy.gravityPull(golem);
		}
		if (++this.bulletTick >= 20) {
			this.bulletTick = 0;
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
			if (CataArmorHelper.guardianLegs(golem))
				bonus += MGConfig.COMMON.gravityChargeArmorBonus.get().floatValue();
			if (CataArmorHelper.monstrosityChest(golem))
				bonus += MGConfig.COMMON.monstrosityChargeBonus.get().floatValue();
			boolean chargeHit = GolemCombatHelper.meleeHit(golem, e, golem.damageSources().mobAttack(golem), dmg * bonus);
			if (chargeHit) {
				ChargeHelper.pushChargeTarget(golem, e, dir);
				SkillSound.playChargeHit(golem);
			}
			GolemCataProxy.inflictStun(golem, e, EnderGuardianRocketPunchModifier.STUN_TICKS);
		}
	}

	/** 末影守卫：冲锋前的后撤改为真瞬移（与它的虚空符文一致）。 */
	@Override
	protected BackstepHelper chargeBackstep(AbstractGolemEntity<?, ?> golem) {
		return new TeleportBackstepHelper(golem);
	}
}
