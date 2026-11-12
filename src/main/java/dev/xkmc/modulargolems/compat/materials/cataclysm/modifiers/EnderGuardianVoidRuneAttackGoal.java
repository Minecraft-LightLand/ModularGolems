package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.cataclysm_mux.MWCataProxy;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.VortexField;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.targeting.TargetManager;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.entity.EntityTypeTest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Void Rune. The ring around the golem is always cast; on top of that the golem sends one row per
 * target toward it. All rows appear together instead of one after another, so a golem facing three
 * targets surrounds each of them at once.
 * <p>
 * With a single target the extra rows are fanned out beside the main row, which keeps the shape the
 * skill had before multi-target rows were introduced.
 */
public class EnderGuardianVoidRuneAttackGoal extends BaseRangedAttackGoal {

	/**
	 * 虚空符文冷却：5 秒。
	 */

	private LivingEntity volleyTarget;
	private int volleysLeft;
	private int volleyTimer;
	private static final int VOLLEY_GAP = 6;

	public EnderGuardianVoidRuneAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(EnderGuardianVoidRuneModifier.COOLDOWN, EnderGuardianVoidRuneModifier.MIN_RANGE, EnderGuardianVoidRuneModifier.MAX_RANGE, golem, lv);
	}

	@Override
	public void start() {
		super.start();
		volleyTarget = null;
		volleysLeft = 0;
		volleyTimer = 0;
	}

	@Override
	public void stop() {
		super.stop();
		volleyTarget = null;
		volleysLeft = 0;
		volleyTimer = 0;
	}

	@Override
	public boolean canContinueToUse() {
		return volleysLeft > 0 || super.canContinueToUse();
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		castVolley(target);
		// 末地守卫胸甲：半血以下连续召唤三次（后续两次延后补上），而不是一次放出三倍排数
		volleyTarget = target;
		volleysLeft = CataArmorHelper.guardianTripleRune(golem) ? EnderGuardianVoidRuneModifier.TRIPLE_EXTRA : 0;
		volleyTimer = VOLLEY_GAP;
		return true;
	}

	@Override
	public void tick() {
		if (volleysLeft > 0) {
			if (--volleyTimer <= 0) {
				LivingEntity t = volleyTarget;
				if (t == null || !t.isAlive() || t.isRemoved()) {
					t = golem.getTarget();
				}
				if (t != null && t.isAlive() && !t.isRemoved()) {
					castVolley(t);
				}
				volleysLeft--;
				volleyTimer = VOLLEY_GAP;
			}
		}
		super.tick();
	}

	/**
	 * 一次完整施法：自身周围一圈 + 每排独立锁定目标。
	 */
	private void castVolley(LivingEntity main) {
		GolemCataProxy.guardianStompFangsRing(golem);
		if (main != null && main.isAlive()) {
			MWCataProxy.spawnVortex(golem, main.position());
			VortexField.add(golem, main.position());
		}
		castRows(golem, main, EnderGuardianVoidRuneModifier.ROWS);
	}

	/**
	 * One row per target, all in the same tick. Targets are chosen once so two rows never share a
	 * quarry; when there are fewer targets than rows the spare rows fan out beside the main one.
	 */
	private static void castRows(AbstractGolemEntity<?, ?> golem, LivingEntity main, int rows) {
		var targets = collectTargets(golem, main);
		for (int i = 0; i < rows; i++) {
			if (i < targets.size()) {
				GolemCataProxy.guardianStompFangsRow(golem, targets.get(i), 0.0F);
			} else {
				float offset = EnderGuardianVoidRuneModifier.SIDE_ANGLE * (1 + (i - targets.size()) / 2);
				if (i % 2 == 1) offset = -offset;
				GolemCataProxy.guardianStompFangsRow(golem, main, offset);
			}
		}
	}

	private static List<LivingEntity> collectTargets(AbstractGolemEntity<?, ?> golem, LivingEntity main) {
		var list = golem.level().getEntities(
				EntityTypeTest.forClass(LivingEntity.class),
				golem.getBoundingBox().inflate(EnderGuardianVoidRuneModifier.SEARCH_RANGE),
				e -> GolemTargetUtil.canTarget(golem, e));
		list.sort(Comparator.comparing((LivingEntity e) -> e == main ? 0 : 1)
				.thenComparing(e -> Optional.ofNullable(
						TargetManager.predicateTarget(golem, e)
				).map(Enum::ordinal).orElse(100)));
		List<LivingEntity> ans = new ArrayList<>();
		for (var e : list) {
			if (ans.size() >= EnderGuardianVoidRuneModifier.ROWS) break;
			ans.add(e);
		}
		if (ans.isEmpty()) ans.add(main);
		return ans;
	}

	/**
	 * 末地守卫胸甲：半血以下（或被胸甲钉在最终形态）时冷却减半。
	 */
	@Override
	protected int getWaitTime() {
		return CataArmorHelper.guardianFastSkill(golem) ? EnderGuardianVoidRuneModifier.COOLDOWN / 2 : EnderGuardianVoidRuneModifier.COOLDOWN;
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
