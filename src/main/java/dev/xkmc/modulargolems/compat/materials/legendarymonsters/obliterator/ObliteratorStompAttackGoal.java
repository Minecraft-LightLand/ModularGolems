package dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

/**
 * 后跳踏地（Boss state28/29）。完全照「扫戟扇形阵」的结构：
 * <ul>
 *   <li>本技能只负责生成冲击波：{@link #performAttack} 一上来就铺最内圈，之后每 {@link ObliteratorStompModifier#RING_INTERVAL}
 *       tick 外扩一圈，半径 {@link ObliteratorStompModifier#WAVE_START}→16，共 {@link ObliteratorStompModifier#RING_COUNT} 圈；</li>
 *   <li>后撤完全交给 {@link #backstep()} 返回的 MG {@link BackstepHelper}，技能里不做任何位移。</li>
 * </ul>
 * 首圈附带镜头抖动 + 爆炸音 + 210° 范围攻击。
 */
public class ObliteratorStompAttackGoal extends BaseRangedAttackGoal {

	/** 当前铺到第几圈，-1 表示本次施法尚未开始。 */
	private int ring = -1;
	private int lastRingTick;
	private float castYaw;
	private double centerX, centerZ;

	public ObliteratorStompAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(ObliteratorStompModifier.COOLDOWN, ObliteratorStompModifier.MIN_RANGE,
				ObliteratorStompModifier.MAX_RANGE, golem, lv);
	}

	/**
	 * 每次调用只铺一圈，铺完返回 true。返回 false 时基类把它当「连发中」保持占用，
	 * 下个 tick 继续调用，从而实现每 2 tick 一圈的推进。
	 */
	@Override
	public boolean canContinueToUse() {
		// 连铺冲击波期间不因目标短暂漂出射程而中断
		return ring >= 0 || super.canContinueToUse();
	}
	@Override
	protected boolean performAttack(LivingEntity target) {
		if (ring < 0) {
			ring = 0;
			lastRingTick = golem.tickCount;
			// 锁定朝目标的朝向，冲击波每圈都从傀儡位置正对目标（否则会歪）
			castYaw = (float) (Mth.atan2(-(target.getX() - golem.getX()), target.getZ() - golem.getZ()) * Mth.RAD_TO_DEG);
			centerX = golem.getX();
			centerZ = golem.getZ();
			golem.setYRot(castYaw);
			golem.yBodyRot = castYaw;
			golem.yHeadRot = castYaw;
			spawnRing(0);
			return false;
		}
		if (golem.tickCount - lastRingTick < ObliteratorStompModifier.RING_INTERVAL) return false;
		ring++;
		lastRingTick = golem.tickCount;
		if (ring >= ObliteratorStompModifier.RING_COUNT) {
			ring = -1;
			return true;
		}
		spawnRing(ring);
		return false;
	}

	private void spawnRing(int index) {
		int distance = ObliteratorStompModifier.WAVE_START + index * ObliteratorStompModifier.WAVE_STEP;
		LMProxy.backstepStompRing(golem, lv, distance, index == 0, castYaw, centerX, centerZ);
	}

	@Override
	public void stop() {
		super.stop();
		ring = -1;
	}

	@Override
	protected BackstepHelper backstep() {
		return new BackstepHelper(golem, 5.0, BackstepHelper.DEFAULT_HEIGHT);   // boss 后撤仅 1.5 格；用较小的后撤距离，别跳到 16 格外
	}

	@Override
	protected double getLeapRange() {
		return BackstepHelper.DEFAULT_LEAP_RANGE;
	}
}
