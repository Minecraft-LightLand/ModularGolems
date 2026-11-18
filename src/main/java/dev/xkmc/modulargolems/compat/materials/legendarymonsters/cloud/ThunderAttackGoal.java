package dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor.LMArmorHelper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import dev.xkmc.modulargolems.util.SkillSound;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 雷电拳：照 Cloud_Golem state31 的三段雷击 —— 第一段一列、第二段三列、第三段双列。
 * <p>
 * 原版单片 {@code performThunderAttack} 只铺一条雷带，和 Boss 的三段递进差得远。
 * 每段都沿「傀儡 → 目标」方向铺 16 根雷柱（间距 1.25、逐根延迟落地）；多列时绕傀儡张开
 * {@link ThunderModifier#SIDE_ANGLE} 弧度（取自 Boss t37 的 ±0.3）。
 * <p>
 * 每列单独索敌：第 i 列打第 i 个目标，目标不够时多余的列才按上面的张角错开。
 */
public class ThunderAttackGoal extends BaseRangedAttackGoal {

	/** 三段雷击的触发 tick（相对施放起点），对应 Boss 的 20 / 37 / 68。 */
	private static final int[] WAVE_TICKS = {0, 20, 40};
	/** 每段的列形态：0 = 一列，1 = 三列，2 = 双列。 */
	private static final int[] WAVE_MODES = {0, 1, 2};

	private int castTicks;
	private int wave;
	private boolean casting;

	public ThunderAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(ThunderModifier.cooldownFor(lv), ThunderModifier.MIN_RANGE,
				ThunderModifier.MAX_RANGE, golem, lv);
	}

	@Override
	public void start() {
		super.start();
		casting = false;
		castTicks = 0;
		wave = 0;
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
	public void tick() {
		if (casting) {
			LivingEntity target = golem.getTarget();
			if (target == null || golem.isRemoved()) {
				casting = false;
			} else {
				if (wave < WAVE_TICKS.length && castTicks >= WAVE_TICKS[wave]) {
					fireWave(target);
					wave++;
				}
				castTicks++;
				if (wave >= WAVE_TICKS.length && castTicks > WAVE_TICKS[WAVE_TICKS.length - 1] + 4) {
					casting = false;
				}
			}
		}
		super.tick();
	}

	/**
	 * 放一段雷：Boss 在每段都会播 {@code LIGHTNING_BOLT_THUNDER}（音量 10）和
	 * {@code DRAGON_FIREBALL_EXPLODE}，并伴随 {@code controlledSmashParticles}；这里保留音效，
	 * 粒子交给客户端在命中点表现。
	 */
	private void fireWave(LivingEntity target) {
		int mode = WAVE_MODES[wave];
		SkillSound.playChargeOnly(golem, SoundEvents.LIGHTNING_BOLT_THUNDER, 3.0F, 1.0F);
		SkillSound.playChargeOnly(golem, SoundEvents.DRAGON_FIREBALL_EXPLODE, 1.0F, 1.2F);
		// 每列单独索敌：第 i 列找第 i 个目标；目标不够时多余的列按 SIDE_ANGLE 张角错开主目标
		// （照灾变虚空符文 castRows 的做法，而不是给每个目标都放一整组）
		castColumns(golem, target, columnsOf(mode), damage());
	}

	/**
	 * 铺 {@code columns} 列雷击，每列单独锁定一个目标。
	 * 目标充足时第 i 列打第 i 个目标；目标不足时多出来的列以主目标方位为轴左右张开，
	 * 张角 {@code SIDE_ANGLE * (1 + (i - targets.size()) / 2)} 弧度，奇偶决定正负。
	 */
	private void castColumns(AbstractGolemEntity<?, ?> golem, LivingEntity main, int columns, float dmg) {
		List<LivingEntity> targets = collectTargets(golem, main);
		for (int i = 0; i < columns; i++) {
			if (i < targets.size()) {
				LMProxy.spawnCloudGolemThunderRow(golem, targets.get(i), 0.0F, dmg);
			} else {
				float angle = ThunderModifier.SIDE_ANGLE * (1 + (i - targets.size()) / 2);
				if (i % 2 == 1) angle = -angle;
				LMProxy.spawnCloudGolemThunderRow(golem, main, angle, dmg);
			}
		}
	}

	/** 收集副目标：主目标优先，其余按 target 优先级排序（照虚空符文 collectTargets）。 */
	private static List<LivingEntity> collectTargets(AbstractGolemEntity<?, ?> golem, LivingEntity main) {
		var list = golem.level().getEntities(
				net.minecraft.world.level.entity.EntityTypeTest.forClass(LivingEntity.class),
				golem.getBoundingBox().inflate(ThunderModifier.SEARCH_RANGE),
				e -> GolemTargetUtil.canTarget(golem, e));
		list.sort(java.util.Comparator.comparing((LivingEntity e) -> e == main ? 0 : 1)
				.thenComparing(e -> java.util.Optional.ofNullable(
						dev.xkmc.modulargolems.content.entity.targeting.TargetManager.predicateTarget(golem, e)
				).map(Enum::ordinal).orElse(100)));
		List<LivingEntity> ans = new ArrayList<>();
		for (var e : list) {
			if (ans.size() >= 3) break;
			ans.add(e);
		}
		if (ans.isEmpty()) ans.add(main);
		return ans;
	}

	/** 本次雷击需要的列数：一列=1、三列=3、双列=2。 */
	private static int columnsOf(int mode) {
		return mode == 1 ? 3 : mode == 2 ? 2 : 1;
	}

	/** Boss 雷柱固定 7.0，按傀儡攻击力等比缩放。 */
	private float damage() {
		// 浮云头盔：雷电拳伤害 +50%
		return LMProxy.bossDamage(golem, lv, 7.0f) * LMArmorHelper.cloudHelmetMult(golem);
	}

	/** 二阶段（≤65% 生命）才开放，对应 Boss state31 只在 angry 状态使用。 */
	@Override
	public boolean canUse() {
		return LMArmorHelper.cloudFinalForm(golem) && super.canUse();
	}
	@Override
	protected boolean performAttack(LivingEntity target) {
		if (casting) return false;
		casting = true;
		castTicks = 0;
		wave = 0;
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
