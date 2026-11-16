package dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.LivingEntity;

/**
 * 灵魂爆破：照 PossessedPaladinEntity state38 t69~83 的 flameRadagonShockwave —— 
 * 由内向外一圈一圈外推，每 2 tick 一圈，distance 从 2 递增到 16（共 8 圈、50 根灵魂柱）。
 * <p>
 * 起手时锁定朝向与圆心，后续圈固定在该点扩散，不跟随傀儡移动。
 */
public class PaladinSoulBlastWaveAttackGoal extends BaseRangedAttackGoal {

	/** 每圈间隔（tick），对应 Boss 的 i += 2。 */
	private static final int RING_INTERVAL = 2;
	/** 起始半径，对应 Boss 的 i - 67（i 从 69 开始）。 */
	private static final int DISTANCE_START = 2;
	/** 终止半径，对应 Boss 的 i 到 83。 */
	private static final int DISTANCE_END = 16;

	private boolean casting;
	private int castTicks;
	private int nextRing;
	private float castYaw;
	private double centerX, centerZ;

	public PaladinSoulBlastWaveAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(PaladinSoulBlastWaveModifier.COOLDOWN, PaladinSoulBlastWaveModifier.MIN_RANGE,
				PaladinSoulBlastWaveModifier.MAX_RANGE, golem, lv);
	}

	@Override
	public void start() {
		super.start();
		casting = false;
		castTicks = 0;
	}

	@Override
	public void stop() {
		super.stop();
		casting = false;
	}

	@Override
	public boolean canContinueToUse() {
		return casting || super.canContinueToUse();
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		if (casting) {
			if (golem.isRemoved()) {
				casting = false;
			} else {
				castTicks++;
				if (nextRing <= DISTANCE_END && (castTicks - 1) % RING_INTERVAL == 0) {
					LMProxy.paladinTridentWave(golem, lv, nextRing, centerX, centerZ, castYaw);
					nextRing += RING_INTERVAL;
				}
				if (nextRing > DISTANCE_END) casting = false;
			}
		}
		super.tick();
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		if (casting) return false;
		// 锁定起手朝向与圆心：整段冲击波固定在该点扩散
		castYaw = (float) Math.toDegrees(Math.atan2(-(target.getX() - golem.getX()), target.getZ() - golem.getZ()));
		golem.setYRot(castYaw);
		golem.yBodyRot = castYaw;
		golem.yHeadRot = castYaw;
		centerX = golem.getX();
		centerZ = golem.getZ();
		castTicks = 0;
		nextRing = DISTANCE_START;
		casting = true;
		return true;
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
