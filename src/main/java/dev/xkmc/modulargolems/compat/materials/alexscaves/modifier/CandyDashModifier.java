package dev.xkmc.modulargolems.compat.materials.alexscaves.modifier;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import dev.xkmc.modulargolems.util.SkillSound;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.function.BiConsumer;

public class CandyDashModifier extends GolemModifier implements ChargeHelper.Modifier {

	public static final int COOLDOWN = 100;
	/** 冲刺最长持续 tick。 */
	public static final int MAX_TICKS = 60;
	/** 寻找冲锋目标的最大距离（格）。 */
	public static final double MAX_RANGE = 15;
	/** 施放的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;
	public static final float BASE_DAMAGE = 7.0F;
	public static final double FOCAL_OFFSET = 1.0D;
	public static final double FOCAL_RANGE_SQR = 7.0D;
	public static final float KNOCKBACK = 2.0F;
	private static final int RING_INTERVAL = 3;

	public CandyDashModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	public static Vec3 flatDir(Vec3 dir) {
		Vec3 flat = new Vec3(dir.x, 0, dir.z);
		return flat.lengthSqr() < 1e-8 ? new Vec3(0, 0, 1) : flat.normalize();
	}

	public static float chargeYaw(Vec3 dir) {
		Vec3 flat = flatDir(dir);
		return (float) (Mth.atan2(-flat.x, flat.z) * Mth.RAD_TO_DEG);
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc", ChargeHelper.chargeReduction()).withStyle(ChatFormatting.GREEN));
	}

	public static Vec3 focalPoint(AbstractGolemEntity<?, ?> golem, Vec3 dir) {
		return golem.position().add(flatDir(dir).scale(FOCAL_OFFSET));
	}

	public static float chargeDamage(Vec3 focal, LivingEntity target) {
		float dist = (float) target.distanceToSqr(focal);
		if (dist >= FOCAL_RANGE_SQR) return 0.0F;
		return BASE_DAMAGE + (BASE_DAMAGE - dist);
	}

	public static void knockBack(Vec3 focal, LivingEntity target) {
		target.knockback(KNOCKBACK, focal.x - target.getX(), focal.z - target.getZ());
	}

	public static void setDashFx(AbstractGolemEntity<?, ?> golem, Vec3 dir) {
		golem.setDashYaw(chargeYaw(dir));
	}

	public static void clearDashFx(AbstractGolemEntity<?, ?> golem) {
		golem.setDashYaw(Float.NaN);
	}

	public static void spawnChargeRing(AbstractGolemEntity<?, ?> golem) {
		if (!golem.level().isClientSide || !golem.isDashing()) return;
		float yaw = golem.getDashYaw();
		double front = golem.getBbWidth() + 0.8D;
		Vec3 pos = new Vec3(0, golem.getEyeHeight() - 0.1D, front).yRot((float) Math.toRadians(-yaw)).add(golem.position());
		golem.level().addAlwaysVisibleParticle(ACParticleRegistry.CANDICORN_CHARGE.get(), true,
				pos.x, pos.y, pos.z, golem.getId(), 0.0D, yaw);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(1, new CandyDashAttackGoal(entity, lv, this));
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, LivingDamageEvent event, int level) {
		if (CandyDashAttackGoal.isCharging(entity)) {
			event.setAmount(event.getAmount() * ChargeHelper.CHARGE_DAMAGE_MULT);
		}
	}

	@Override
	public void onClientTick(AbstractGolemEntity<?, ?> golem, int level) {
		if (golem.tickCount % RING_INTERVAL == 0) {
			spawnChargeRing(golem);
		}
	}

	@Override
	public void setCharging(ChargeHelper.Instance ins, boolean on) {
		ins.owner().setTransientFlag(CandyDashAttackGoal.KEY_ACTIVE, on);
		if (on) {
			setDashFx(ins.owner(), ins.owner().getLookAngle());
		} else {
			clearDashFx(ins.owner());
		}
	}

	@Override
	public void performCharge(ChargeHelper.Instance ins) {
		SkillSound.playCharge(ins.owner(), ACSoundRegistry.CANDICORN_CHARGE_START.get(), 1.0F, 1.0F);
	}

	@Override
	public void tickCharge(ChargeHelper.Instance ins) {
		setDashFx(ins.owner(), ins.owner().getLookAngle());
		if (ins.owner().tickCount % 12 == 0) {
			SkillSound.playChargeOnly(ins.owner(), ACSoundRegistry.CANDICORN_GALLOP.get(), 0.6F, 1.0F);
		}
	}

	@Override
	public void endCharge(ChargeHelper.Instance ins) {
		clearDashFx(ins.owner());
	}

	@Override
	public boolean hasChargeParticle() {
		return false;
	}

	@Override
	public int getCooldown(AbstractGolemEntity<?, ?> golem, int lv) {
		return COOLDOWN;
	}

	@Override
	public double getChargeDistance(AbstractGolemEntity<?, ?> golem, int lv) {
		return 12;
	}

	@Override
	public float chargeDamage(ChargeHelper.Instance ins, LivingEntity target) {
		var golem = ins.owner();
		return chargeDamage(focalPoint(golem, golem.getLookAngle()), target);
	}

	@Override
	public void onChargeTouch(ChargeHelper.Instance ins, LivingEntity target) {
		var golem = ins.owner();
		knockBack(focalPoint(golem, golem.getLookAngle()), target);
	}

}
