package dev.xkmc.modulargolems.content.modifier.special;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * 后撤步的瞬移版本（通用）：不向后跳，而是直接向后瞬移 {@link #DISTANCE} 格（贴地）。
 * <p>
 * 供需要"真瞬移"的傀儡使用（如灾变末影守卫、湮灭构造体）。用 vanilla 末影粒子
 * {@link ParticleTypes#PORTAL} + 末影人传送音，不依赖任何兼容模组的粒子。
 * <p>
 * 触发距离沿用 {@link BackstepHelper#canUse}：远程模式用技能给的 leap range，
 * 近战模式用自身近战 reach 减半格。与跳跃版共用同一套 busy 锁语义：
 * {@link #start()} 抢占并瞬移，{@link #tick()} 下一帧立刻释放。
 */
public class TeleportBackstepHelper extends BackstepHelper {

	/** 后撤瞬移距离（格）。 */
	public static final double DISTANCE = 6.0;

	protected final AbstractGolemEntity<?, ?> golem;
	private boolean pending;

	public TeleportBackstepHelper(AbstractGolemEntity<?, ?> golem) {
		super(golem, DISTANCE, DEFAULT_HEIGHT);
		this.golem = golem;
	}

	@Override
	public boolean canUse(LivingEntity target, double range) {
		return golem.onGround() && !EarthquakeHelper.isBusy(golem) && super.canUse(target, range);
	}

	/** 瞬移特效：可在子类覆写成模组专属粒子。 */
	protected void playTeleportFx(double x, double y, double z) {
		var level = golem.level();
		level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.ENDERMAN_TELEPORT, SoundSource.NEUTRAL, 3.0F, 1.0F);
		if (level instanceof ServerLevel sl) {
			sl.sendParticles(ParticleTypes.PORTAL, x, y + 1.0, z, 12, 0.4, 0.8, 0.4, 0.0);
		}
	}

	@Override
	public void start() {
		Vec3 back = golem.getForward();
		back = new Vec3(back.x, 0, back.z);
		if (back.lengthSqr() < 1.0E-6) back = new Vec3(0, 0, 1);
		else back = back.normalize().scale(-1.0);
		double tx = golem.getX() + back.x * DISTANCE;
		double tz = golem.getZ() + back.z * DISTANCE;
		Vec3 at = EarthquakeHelper.findFloor(golem.level(), new Vec3(tx, golem.getY(), tz), 8);
		double ty = at == null ? golem.getY() : at.y;
		playTeleportFx(golem.getX(), golem.getY(), golem.getZ());
		if (golem.randomTeleport(tx, ty, tz, true)) {
			golem.getNavigation().stop();
			playTeleportFx(golem.getX(), golem.getY(), golem.getZ());
		}
		EarthquakeHelper.setBusy(golem, true);
		pending = true;
	}

	/**
	 * 瞬移是贴地完成的，实体永远不会离地。基类（如 {@code BaseChargeGoal#tickBackstep}）
	 * 会先等 {@link #isAirborne()} 为真再判落地，这里直接返回 true 表示「腾空阶段已完成」，
	 * 否则冲锋的前置后撤会永远卡在等离地、busy 锁不放、AI 卡死。
	 */
	@Override
	public boolean isAirborne() {
		return true;
	}

	@Override
	public boolean tick() {
		if (!pending) return false;
		pending = false;
		EarthquakeHelper.setBusy(golem, false);
		golem.meleeGoal.clearDelay();
		return true;
	}

	@Override
	public void cancel() {
		pending = false;
		EarthquakeHelper.setBusy(golem, false);
	}

}
