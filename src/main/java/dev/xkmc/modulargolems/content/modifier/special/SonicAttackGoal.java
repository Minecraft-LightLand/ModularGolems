package dev.xkmc.modulargolems.content.modifier.special;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.ranged.SonicCannonItem;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class SonicAttackGoal extends BaseRangedAttackGoal {

	private static final double BEAM_LENGTH = 16;
	private static final int CHARGE_DELAY = 34;

	private int chargeTicks = 0;
	private Vec3 targetPos;

	public SonicAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(200, 0, 16, golem, lv);
	}

	@Override
	public boolean canUse() {
		if (golem.getMainHandItem().getItem() instanceof SonicCannonItem) return false;
		return super.canUse();
	}

	@Override
	public void tick() {
		long time = golem.level().getGameTime();
		LivingEntity le = golem.getTarget();
		if (this.attackTime <= time && le != null && le.isAlive()) {
			if (chargeTicks == 0) {
				chargeTicks = CHARGE_DELAY;
				golem.playSound(SoundEvents.WARDEN_SONIC_CHARGE, 3.0F, 1.0F);
			} else {
				chargeTicks--;
			}
			targetPos = le.getEyePosition();
		}
		super.tick();
	}

	@Override
	protected boolean performAttack(LivingEntity le) {
		if (chargeTicks > 0) return false;
		Vec3 src = golem.position().add(0.0D, golem.getBbHeight() * 0.6F, 0.0D);
		Vec3 dst = targetPos != null ? targetPos : le.getEyePosition();
		Vec3 dir = dst.subtract(src).normalize();
		targetPos = null;
		for (int i = 1; i < 17; ++i) {
			Vec3 vec33 = src.add(dir.scale(i));
			if (golem.level() instanceof ServerLevel level)
				level.sendParticles(ParticleTypes.SONIC_BOOM, vec33.x, vec33.y, vec33.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
		}
		golem.playSound(SoundEvents.WARDEN_SONIC_BOOM, 3.0F, 1.0F);
		var attr = golem.getAttribute(GolemTypes.GOLEM_SWEEP.holder());
		List<LivingEntity> target = new ArrayList<>();
		if (attr != null && attr.getValue() > 0) {
			AABB aabb = new AABB(src, src.add(dir.scale(BEAM_LENGTH)));
			for (var e : golem.level().getEntities(golem, aabb)) {
				if (e instanceof LivingEntity x && GolemTargetUtil.canTarget(golem, x)) {
					target.add(x);
				}
			}
		}
		if (GolemTargetUtil.canTarget(golem, le) && !target.contains(le)) {
			target.add(le);
		}
		for (var e : target) {
			e.hurt(golem.level().damageSources().sonicBoom(golem), 10 * lv);
			double d1 = 0.5D * (1.0D - e.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
			double d0 = 2.5D * (1.0D - e.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
			e.push(dir.x() * d0, dir.y() * d1, dir.z() * d0);
		}
		return true;
	}

	@Override
	public void stop() {
		chargeTicks = 0;
		targetPos = null;
		super.stop();
	}

	@Override
	protected BackstepHelper backstep() {
		return new BackstepHelper(golem, BackstepHelper.DEFAULT_DISTANCE, BackstepHelper.DEFAULT_HEIGHT);
	}

	@Override
	protected double getLeapRange() {
		return BackstepHelper.DEFAULT_LEAP_RANGE;
	}

}
