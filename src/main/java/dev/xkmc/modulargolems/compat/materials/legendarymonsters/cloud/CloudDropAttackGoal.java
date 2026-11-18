package dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor.LMArmorHelper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
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
 * 浮云落阵：头顶一圈一圈排出云，云落地后炸开。
 * <p>
 * 与一字落云同理，云只做视觉（CloudEntity 会把爆炸范围内所有生物都打一遍），
 * 伤害在这里按 {@link GolemTargetUtil#canTarget} 结算。
 */
public class CloudDropAttackGoal extends BaseRangedAttackGoal {

	/** 云从生成到落地所需的 tick。 */
	private static final int FALL_TICKS = 12;

	private final List<PendingDrop> pending = new ArrayList<>();

	private int castTicks;
	private int ringsDone;
	private boolean casting;
	private double centerX, centerZ;

	private record PendingDrop(double x, double y, double z, int ticksLeft) {
	}

	public CloudDropAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(CloudDropModifier.COOLDOWN, CloudDropModifier.MIN_RANGE, CloudDropModifier.MAX_RANGE, golem, lv);
	}

	@Override
	public void start() {
		super.start();
		casting = false;
		castTicks = 0;
		ringsDone = 0;
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
			if (castTicks % CloudDropModifier.RING_GAP == 0) {
				dropRing(ringsDone);
				ringsDone++;
				if (ringsDone >= CloudDropModifier.RING_COUNT) casting = false;
			}
		}
		tickPending();
		super.tick();
	}

	/** 第 {@code ring} 圈：{@code 6 + 3 * ring} 团云均匀铺在半径 {@code RING_START + ring * RING_STEP} 的圆上。 */
	private void dropRing(int ring) {
		int count = 6 + ring * 3;
		double radius = CloudDropModifier.RING_START + ring * CloudDropModifier.RING_STEP;
		// 落点锚地面：傀儡位置可能还在半空，直接用 getY() 会把云和爆炸画在空中

		// 落点锚地面：傀儡位置可能还在半空，直接用 getY() 会把云和爆炸画在空中
		// 伤害随云自带、由 CloudEntity 按 Boss 逻辑落地结算（碰撞箱 inflate(CLOUD_RADIUS) + 距离衰减）
		// 浮云头盔：落云伤害 +50%
		int dmg = (int) Math.max(1, GolemCombatHelper.getAttackDamage(golem) * CloudDropModifier.CLOUD_DAMAGE
				* LMArmorHelper.cloudHelmetMult(golem));
		for (int i = 0; i < count; i++) {
			double ang = i * Math.PI * 2 / count + Math.PI * 2 / (count + 1);
			double x = centerX + Math.cos(ang) * radius;
			double z = centerZ + Math.sin(ang) * radius;
			// 每团云用自身落点地面高度生成（不再用圆心地面，地形不平时判定/落点才对得上）
			var floor = EarthquakeHelper.findFloor(golem.level(), new Vec3(x, golem.getY(), z), 8);
			double ground = floor == null ? golem.getY() : floor.y;
			LMProxy.spawnCloudGolemCloud(golem, x, ground + CloudDropModifier.CLOUD_HEIGHT, z, dmg, CloudDropModifier.CLOUD_RADIUS);
		}
		// Boss state9 只在开头播一次 ENCHANTMENT_TABLE_USE(3, 0.769)，不逐圈重复
		if (ring == 0) SkillSound.playChargeOnly(golem, SoundEvents.ENCHANTMENT_TABLE_USE, 3.0F, 0.769F);
	}

	private void tickPending() {
		if (pending.isEmpty()) return;
		List<PendingDrop> keep = new ArrayList<>();
		Iterator<PendingDrop> it = pending.iterator();
		while (it.hasNext()) {
			PendingDrop d = it.next();
			int left = d.ticksLeft() - 1;
			if (left <= 0) settle(d);
			else keep.add(new PendingDrop(d.x(), d.y(), d.z(), left));
		}
		pending.clear();
		pending.addAll(keep);
	}

	private void settle(PendingDrop d) {
		float atk = GolemCombatHelper.getAttackDamage(golem);
		var src = golem.damageSources().mobAttack(golem);
		float mult = LMArmorHelper.cloudHelmetMult(golem);
		double r = CloudDropModifier.CLOUD_RADIUS;
		AABB box = new AABB(d.x() - r, d.y() - 1, d.z() - r, d.x() + r, d.y() + 3, d.z() + r);
		for (LivingEntity e : golem.level().getEntitiesOfClass(LivingEntity.class, box)) {
			if (!GolemTargetUtil.canTarget(golem, e)) continue;
			if (e.distanceToSqr(d.x(), e.getY(), d.z()) > r * r) continue;
			GolemCombatHelper.meleeHit(golem, e, src, atk * CloudDropModifier.CLOUD_DAMAGE * mult);
		}
	}

	/** 一阶段（>65% 生命）才开放，对应 Boss state9 只在非 angry 状态使用；穿浮云胸甲时不受此限。 */
	@Override
	public boolean canUse() {
		return LMArmorHelper.cloudFirstPhaseSkill(golem) && super.canUse();
	}
	@Override
	protected boolean performAttack(LivingEntity target) {
		if (casting) return false;
		castTicks = 0;
		ringsDone = 0;
		casting = true;
		// 锁定起手圆心：多圈云固定在释放点排布，不跟随傀儡移动
		centerX = golem.getX();
		centerZ = golem.getZ();
		SkillSound.playChargeOnly(golem, SoundEvents.DRAGON_FIREBALL_EXPLODE, 1.5F, 0.9F);
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
