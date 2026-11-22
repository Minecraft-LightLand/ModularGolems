package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.content.modifier.special.BaseChargeGoal;

import dev.xkmc.modulargolems.content.modifier.special.ICooldownSkill;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataDispatch;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import dev.xkmc.modulargolems.util.SkillSound;
import dev.xkmc.modulargolems.util.GolemCombatHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class IgnisChargeGoal extends BaseChargeGoal {

	public static final String KEY_ACTIVE = "IgnisCharging";
	private static final String KEY_TIME = "IgnisChargeTime";

	private static final int MAX_TICKS = 60, COOLDOWN = 200;
	private static final double RANGE = 16;

	private final AbstractGolemEntity<?, ?> golem;
	private Vec3 start, dir;
	private double total;
	private int ticks;
	private boolean blocked;

	public IgnisChargeGoal(AbstractGolemEntity<?, ?> golem, int lv, ChargeHelper.Modifier mod) {
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
		// canUse 已排除空目标，但所有 canUse 都先于 start 执行，中间目标可能已死，
		// 这里直接退出而不是解引用。
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
		ChargeHelper.spawnChargeParticles(golem, dir, particle());
		ChargeHelper.playChargeEasterEgg(golem);
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
		if (this.ticks % 4 == 0) {
			ChargeHelper.spawnChargeParticles(golem, dir, particle());
		}
		if (!ChargeHelper.moveOneTick(golem, dir)) {
			this.blocked = true;
			return;
		}
		float atk = (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE);
		if (CataDispatch.ignisBlue(golem)) atk *= 4f / 3;
		AABB aabb = golem.getBoundingBox().inflate(0.5);
		for (var e : golem.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
			if (!GolemTargetUtil.canTarget(golem, e)) continue;
			float dmg = atk * 1.1f + e.getMaxHealth() * 0.05f;
			float bonus = 1F;
			if (golem.getItemBySlot(EquipmentSlot.CHEST).is(CataCompatRegistry.MONSTROSITY_CHESTPLATE.get()))
				bonus += MGConfig.COMMON.monstrosityChargeBonus.get().floatValue();
			var src = golem.damageSources().mobAttack(golem);
			boolean hit = e.hurt(src, dmg * bonus);
			GolemCombatHelper.breakShield(golem, e, src);
			GolemCataProxy.inflictStun(golem, e, 60);
			// AI 冲锋不经过 onChargeTouch（那只在骑乘冲锋路径里调用），所以音效要在这里播。
			if (hit) {
				SkillSound.playChargeOnly(golem, SoundEvents.ANVIL_LAND, 1.5F, 0.8F + golem.getRandom().nextFloat() * 0.1F);
			}
		}
	}

	private ParticleOptions particle() {
		return CataDispatch.ignisBlue(golem) ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME;
	}

	private LivingEntity findTarget() {
		return ChargeHelper.findChargeTarget(golem, RANGE);
	}

}
