package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataCompatRegistry;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemPartType;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;

public class HarbingerHomingMissileAttackGoal extends BaseRangedAttackGoal {

	private static final int SHOT_COUNT = 3;
	private static final int FAR_RANGE = 35;

	private int step = -1;
	private final List<LivingEntity> targets = new ArrayList<>();

	public HarbingerHomingMissileAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(100, 0, 35, golem, lv);
	}

	@Override
	public void start() {
		super.start();
		collectTargets(golem.getTarget());
	}

	@Override
	public void stop() {
		// super releases the EarthquakeHelper busy lock that the backstep holds, see ScyllaLineLightningAttackGoal
		super.stop();
		step = -1;
		targets.clear();
	}

	@Override
	protected boolean performAttack(LivingEntity target) {

		if (step < 0) step = 0;
		step++;
		int shot = -1;
		if (step == 1 || step == 5 || step == 9) shot = (step - 1) / 4;
		else if (lv >= 2 && (step == 21 || step == 25 || step == 29)) shot = (step - 21) / 4;
		else if (step >= (lv >= 2 ? 30 : 10)) {
			step = -1;
			return true;
		}
		if (shot >= 0) fireOne(pick(shot), shot);
		return false;
	}

	private LivingEntity pick(int shot) {
		if (targets.isEmpty()) collectTargets(golem.getTarget());
		if (targets.isEmpty()) return golem.getTarget();
		return GolemTargetUtil.resolve(golem, targets.get(shot % targets.size()));
	}

	private void collectTargets(LivingEntity main) {
		GolemTargetUtil.collect(golem, main, FAR_RANGE, SHOT_COUNT, targets);
	}

	private void fireOne(LivingEntity target, int j) {
		float w = 0.4F * golem.getBbWidth();
		float y = 0.6F * golem.getEyeHeight() + j * 0.05F;
		if (golem.getType() == GolemTypes.ENTITY_GOLEM.get()) {
			boolean left = golem.getMaterials().get(MetalGolemPartType.LEFT.ordinal())
					.modifiers().containsKey(CataCompatRegistry.HARBINGER_MISSILE.get());
			boolean right = golem.getMaterials().get(MetalGolemPartType.RIGHT.ordinal())
					.modifiers().containsKey(CataCompatRegistry.HARBINGER_MISSILE.get());

			if (left && right) addMissile(target, y, j % 2 == 0 ? -w : w);
			else if (left) addMissile(target, y, -w);
			else if (right) addMissile(target, y, w);
		} else {
			addMissile(target, y, j % 2 == 0 ? -w : w);
		}
	}

	private void addMissile(LivingEntity target, float y, float r) {
		GolemCataProxy.addMissile(golem, target, golem.position().add(
				r * Math.cos(golem.yBodyRot * Mth.DEG_TO_RAD),
				y,
				r * Math.sin(golem.yBodyRot * Mth.DEG_TO_RAD)
		));
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
