package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.MWCataProxy;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * 扫戟扇形阵：向前方扇形区域召唤大量幻影战戟。
 * <p>
 * 复刻灾变咒翼灵骸的 {@code Maledictus_Entity#radagonskill(spreadarc, distance, delay)}
 * （招式 attackState 26）：本体在 attackTicks 44/46/48/…/58 各铺一圈，也就是每 2 tick 外扩 2 格，
 * 半径从 2 一路到 16，每圈张角 0.7π（约 126°），弧上按一个方块一点均匀排开。
 * <p>
 * 起点与朝向在起手那一刻锁定，各圈都从同一点展开——本体每圈实时读自己的坐标，跟过去的话
 * 傀儡一转身扇形就歪了。
 * <p>
 * 本体这一招是先跳起再落地释放；傀儡侧改为先向后撤一步拉开距离，落地后再铺开。
 */
public class MaledictusHalberdFanAttackGoal extends BaseRangedAttackGoal {

	/** 本体每 2 tick 铺下一圈。 */
	private static final int RING_INTERVAL = 2;
	/** 本体每一圈都用同一个 warmup 延迟。 */
	private static final int HALBERD_DELAY = 2;

	/** 当前铺到第几圈，-1 表示本次施法尚未开始。 */
	private int ring = -1;
	private int lastRingTick;
	private Vec3 center;
	private float yaw;

	public MaledictusHalberdFanAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(MaledictusHalberdFanModifier.COOLDOWN, MaledictusHalberdFanModifier.MIN_RANGE, MaledictusHalberdFanModifier.MAX_RANGE, golem, lv);
	}

	@Override
	public boolean canUse() {
		// 半血以下可使用；穿咒魂胸甲时「无视生命值要求」。与本体一致，必须站在地面施放。
		if (!CataArmorHelper.maledictusFinalForm(golem)) return false;
		if (!golem.onGround()) return false;
		return super.canUse();
	}

	/**
	 * 每次调用只铺一圈，铺完返回 true。返回 false 时基类会把它当成「连发中」并保持占用，
	 * 于是下个 tick 继续调用，从而实现本体那种每 2 tick 一圈的推进。
	 */
	@Override
	protected boolean performAttack(LivingEntity target) {
		if (ring < 0) {
			center = golem.position();
			// 朝向目标，而不是傀儡自身前方
			yaw = CataArmorHelper.yawToTarget(golem, target);
			CataArmorHelper.faceYaw(golem, yaw);
			ring = 0;
			lastRingTick = golem.tickCount;
			spawnRing(0);
			return false;
		}
		if (golem.tickCount - lastRingTick < RING_INTERVAL) return false;
		ring++;
		lastRingTick = golem.tickCount;
		if (ring >= MaledictusHalberdFanModifier.RING_COUNT) {
			ring = -1;
			return true;
		}
		spawnRing(ring);
		return false;
	}

	private void spawnRing(int index) {
		int distance = MaledictusHalberdFanModifier.START_DISTANCE + index * MaledictusHalberdFanModifier.STEP;
		MWCataProxy.spawnHalberdFan(golem, center, yaw, distance, MaledictusHalberdFanModifier.SPREAD_ARC, HALBERD_DELAY);
	}

	@Override
	public void stop() {
		super.stop();
		// 扇形阵中途被打断时清掉进度，下次重新从最内圈开始
		ring = -1;
		center = null;
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
