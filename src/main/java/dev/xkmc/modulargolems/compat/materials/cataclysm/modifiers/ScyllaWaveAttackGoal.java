package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

/**
 * 霆浪锚的两段海浪：先向后撤一步拉开距离，落地后放出第一道浪，间隔 {@link #WAVE2_DELAY} tick 再放第二道。
 * 由 {@link ScyllaWaveModifier} 在凑够格挡次数后置位。
 * <p>
 * 中心与朝向在起手（落地）瞬间锁定，两段浪都从同一点发出；否则第二段会跟着傀儡跑到它当时的位置。
 */
public class ScyllaWaveAttackGoal extends Goal {

	private static final int WAVE2_DELAY = 6;

	private final AbstractGolemEntity<?, ?> golem;
	private int timer = -1;
	private Vec3 center;
	private float yaw;
	private BackstepHelper backstep;
	private boolean inited;

	public ScyllaWaveAttackGoal(AbstractGolemEntity<?, ?> golem) {
		this.golem = golem;
	}

	public ScyllaWaveAttackGoal withName(String key) {
		return this;
	}

	@Override
	public boolean canUse() {
		return !golem.level().isClientSide() && ScyllaWaveModifier.isReady(golem);
	}

	@Override
	public void start() {
		ScyllaWaveModifier.clearReady(golem);
		inited = false;
		timer = -1;
		center = null;
	}

	@Override
	public boolean canContinueToUse() {
		if (!inited || backstep != null) return true;
		return timer >= 0;
	}

	/**
	 * 序列按 tick 推进，必须每 tick 都跑：Mob 隔 tick 只 tick {@code requiresUpdateEveryTick()} 为 true
	 * 的 Goal，不声明的话第二道浪会慢一倍。
	 */
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
			// 落地后再放浪；后撤途中不推进
			if (backstep.tick()) {
				backstep = null;
				launch();
			}
			return;
		}
		if (timer >= 0 && --timer <= 0) {
			GolemCataProxy.scyllaSummonWave2(golem, center, yaw);
			timer = -1;
		}
	}

	private void launch() {
		center = golem.position();
		// 朝向目标，而不是傀儡自身前方
		yaw = CataArmorHelper.yawToTarget(golem, golem.getTarget());
		CataArmorHelper.faceYaw(golem, yaw);
		GolemCataProxy.scyllaSummonWave(golem, center, yaw);
		timer = WAVE2_DELAY;
	}

	@Override
	public void stop() {
		if (backstep != null) {
			backstep.cancel();
			backstep = null;
		}
		timer = -1;
		center = null;
	}

}
