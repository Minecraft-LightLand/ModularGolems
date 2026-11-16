package dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

/**
 * 格挡反击的反击段：照灾变「霆浪锚」{@code ScyllaWaveAttackGoal}——
 * 先向后撤一步拉开距离，落地后横扫正面（Boss state5 t27，碎魂 + 碎甲 + 眩晕 55），
 * 3 tick 后再投出 6 把灵魂匕首并补一次扇形范围伤害（Boss t30）。
 * 由 {@link PaladinSoulShieldModifier} 在格挡窗口结束后置位。
 */
public class PaladinSoulShieldAttackGoal extends Goal {

	private final AbstractGolemEntity<?, ?> golem;
	private final int lv;
	private BackstepHelper backstep;
	private boolean inited;
	private boolean done;

	public PaladinSoulShieldAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		this.golem = golem;
		this.lv = lv;
	}

	public PaladinSoulShieldAttackGoal withName(String key) {
		return this;
	}

	@Override
	public boolean canUse() {
		return !golem.level().isClientSide() && PaladinSoulShieldModifier.isReady(golem);
	}

	@Override
	public void start() {
		PaladinSoulShieldModifier.clearReady(golem);
		inited = false;
		done = false;
		backstep = null;
	}

	@Override
	public boolean canContinueToUse() {
		if (!inited || backstep != null) return true;
		return !done || timer >= 0;
	}

	/** 序列按 tick 推进，必须每 tick 都跑。 */
	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		if (!inited) {
			inited = true;
			LivingEntity target = golem.getTarget();
			BackstepHelper b = new BackstepHelper(golem, BackstepHelper.DEFAULT_DISTANCE, BackstepHelper.DEFAULT_HEIGHT);
			if (target != null && b.canUse(target, BackstepHelper.DEFAULT_LEAP_RANGE)) {
				backstep = b;
				b.start();
				return;
			}
			launch();
			return;
		}
		if (backstep != null) {
			// 落地后再放反击；后撤途中不推进
			if (backstep.tick()) {
				backstep = null;
				launch();
			}
			return;
		}
		if (timer >= 0 && --timer <= 0) {
			// Boss state5 t30：斩击 3 tick 后投出幻影匕首 + 范围伤害判定（不再重复斩击）
			LMProxy.paladinDaggerVolley(golem, golem.getTarget(), lv);
			timer = -1;
			done = true;
		}
	}

	private int timer = -1;

	private void launch() {
		// Boss state5 t27：横扫正面（只斩一次），3 tick 后由 timer 分支投出幻影匕首
		LMProxy.paladinCounterAttack(golem, golem.getTarget(), lv);
		timer = 3;
	}

	@Override
	public void stop() {
		if (backstep != null) {
			backstep.cancel();
			backstep = null;
		}
		done = false;
		timer = -1;
	}

}
