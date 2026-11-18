package dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor.LMArmorHelper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import dev.xkmc.modulargolems.util.GolemCombatHelper;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import dev.xkmc.modulargolems.util.SkillSound;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 一字落云：沿傀儡到目标的方向，每隔 {@link #DROP_GAP} tick 降下一团云，云落地后炸开。
 * <p>
 * 云只做视觉，伤害由这里结算：CloudEntity 会把爆炸范围内的所有生物都打一遍、只跳过施法者，
 * 把傀儡设成 owner 就会连友军和主人的宠物一起打。所以云以无主形式生成，命中判定走
 * {@link GolemTargetUtil#canTarget}。
 */
public class LineCloudAttackGoal extends BaseRangedAttackGoal {

	/** 相邻两团云的下落间隔（tick）。 */
	private static final int DROP_GAP = 2;
	/** 云从生成到落地所需的 tick，用来把视觉与结算对上。 */
	private static final int FALL_TICKS = 15;

	/** 已生成、尚未落地的云。落地时结算范围伤害。 */
	private final List<PendingDrop> pending = new ArrayList<>();

	private int castTicks;
	private int dropped;
	private boolean casting;
	private double baseX, baseZ, dirX, dirZ, topY;

	private record PendingDrop(double x, double y, double z, int ticksLeft) {
	}

	public LineCloudAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(LineCloudModifier.cooldownFor(lv), LineCloudModifier.MIN_RANGE, LineCloudModifier.MAX_RANGE, golem, lv);
	}

	@Override
	public void start() {
		super.start();
		casting = false;
		dropped = 0;
		castTicks = 0;
		pending.clear();
	}

	@Override
	public void stop() {
		super.stop();
		casting = false;
		pending.clear();
	}

	@Override
	public boolean canContinueToUse() {
		return casting || !pending.isEmpty() || super.canContinueToUse();
	}

	@Override
	public void tick() {
		if (casting && !golem.isRemoved()) {
			castTicks++;
			if (castTicks % DROP_GAP == 0) {
				dropOne();
				if (dropped >= LineCloudModifier.CLOUD_COUNT) casting = false;
			}
		}
		tickPending();
		super.tick();
	}

	/**
	 * 与 Boss state20 的 {@code summonControlledCloud} 一致：沿「傀儡 → 目标」方向固定距离落云，
	 * 距离是 4、6、8…24，**不随目标距离截断**。目标比 24 格近时，后面的云会越过目标继续往前落。
	 */
	private void dropOne() {
		LivingEntity target = golem.getTarget();
		if (target == null) {
			casting = false;
			return;
		}
		double dist = LineCloudModifier.CLOUD_START + dropped * LineCloudModifier.CLOUD_STEP;
		double x = baseX + dirX * dist;
		double z = baseZ + dirZ * dist;
		double y = topY + LineCloudModifier.CLOUD_HEIGHT;
		LMProxy.spawnCloudGolemCloud(x, y, z, golem.level());
		double ground = golem.level().getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				net.minecraft.util.Mth.floor(x), net.minecraft.util.Mth.floor(z));
		pending.add(new PendingDrop(x, ground, z, FALL_TICKS));
		dropped++;
	}

	private void tickPending() {
		if (pending.isEmpty()) return;
		Iterator<PendingDrop> it = pending.iterator();
		List<PendingDrop> keep = new ArrayList<>();
		while (it.hasNext()) {
			PendingDrop d = it.next();
			int left = d.ticksLeft() - 1;
			if (left <= 0) settle(d);
			else keep.add(new PendingDrop(d.x(), d.y(), d.z(), left));
		}
		pending.clear();
		pending.addAll(keep);
	}

	/** 一团云落地：结算半径内的范围伤害。 */
	private void settle(PendingDrop d) {
		float atk = GolemCombatHelper.getAttackDamage(golem);
		var src = golem.damageSources().mobAttack(golem);
		// 浮云头盔：落云伤害 +50%
		float mult = LMArmorHelper.cloudHelmetMult(golem);
		double r = LineCloudModifier.CLOUD_RADIUS;
		AABB box = new AABB(d.x() - r, d.y() - 1, d.z() - r, d.x() + r, d.y() + 3, d.z() + r);
		for (LivingEntity e : golem.level().getEntitiesOfClass(LivingEntity.class, box)) {
			if (!GolemTargetUtil.canTarget(golem, e)) continue;
			if (e.distanceToSqr(d.x(), e.getY(), d.z()) > r * r) continue;
			GolemCombatHelper.meleeHit(golem, e, src, atk * LineCloudModifier.CLOUD_DAMAGE * mult);
		}
		// 云落地音：LM 的 CloudEntity.onHit 就是播 SHULKER_BULLET_HURT(3, 1)
	}

	/** 一阶段（>65% 生命）才开放，对应 Boss state20 只在非 angry 状态使用；穿浮云胸甲时不受此限。 */
	@Override
	public boolean canUse() {
		return LMArmorHelper.cloudFirstPhaseSkill(golem) && super.canUse();
	}
	@Override
	protected boolean performAttack(LivingEntity target) {
		if (casting) return false;
		castTicks = 0;
		dropped = 0;
		casting = true;
		// 锁定起手基点/方向/目标顶高：整排云沿固定直线落下，不跟随傀儡或目标移动
		baseX = golem.getX();
		baseZ = golem.getZ();
		topY = target.getBoundingBox().maxY;
		double ldx = target.getX() - baseX, ldz = target.getZ() - baseZ; double llen = Math.sqrt(ldx*ldx + ldz*ldz);
		if (llen < 1.0E-4) { dirX = 1.0; dirZ = 0.0; } else { dirX = ldx / llen; dirZ = ldz / llen; }
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
