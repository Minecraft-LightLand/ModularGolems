package dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin;

import dev.xkmc.modulargolems.content.modifier.special.BurstRangedGoal;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor.LMArmorHelper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;

/**
 * 幻影匕首：复刻 {@code PosessedPaladinEntity::throwPhantomDaggers}——朝目标扇形甩出
 * {@link PaladinSoulDaggerModifier#DAGGER_COUNT} 把追踪匕首。
 * <p>
 * 与其他 {@code BurstRangedGoal} 技能一致：{@code getBurstCount()} 就是匕首把数，
 * 每发只生成一把（{@link LMProxy#spawnSinglePhantomDagger}），横向偏移用弹幕的
 * {@code lateral}（-0.5 / 0 / +0.5）换算成角度，形成 Boss 那套 30° 扇形。
 */
public class PaladinSoulDaggerAttackGoal extends BurstRangedGoal {

	/** 相邻两把匕首的角度差（本体 30°）。 */
	private static final float SPREAD_DEG = 30.0F;

	public PaladinSoulDaggerAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(PaladinSoulDaggerModifier.cooldownFor(lv), PaladinSoulDaggerModifier.MIN_RANGE,
				PaladinSoulDaggerModifier.MAX_RANGE, golem, lv);
	}

	@Override
	protected int getBurstCount() {
		return PaladinSoulDaggerModifier.DAGGER_COUNT;
	}

	/**
	 * 三把匕首**同一 tick 齐射**（照 {@code MaledictusArrowAttackGoal} 的写法：{@code getBurstDelay() == 0}
	 * 表示整轮齐射一次性打完），不是鸣雷槊那种"首发领射、其余跟上"。
	 */
	@Override
	protected int getBurstDelay() {
		return 0;
	}

	@Override
	protected void fire(LivingEntity target, float lateral) {
		LMProxy.paladinDaggerSound(golem);
		// 堕魂头盔：每把匕首各自锁定本轮分配到的目标（target 由 BurstRangedGoal 逐发分配），
		// 而不是三把都追同一个主目标。未穿头盔时传 null，保持"回旋到傀儡本体"的旧行为。
		LivingEntity homing = LMArmorHelper.paladinHelmet(golem) ? target : null;
		// lateral 是 -0.5 / 0 / +0.5 的槽位，乘 2 得到 -1 / 0 / +1 的角度序号
		LMProxy.spawnSinglePhantomDagger(golem, target, lv, lateral * 2 * SPREAD_DEG, homing);
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
