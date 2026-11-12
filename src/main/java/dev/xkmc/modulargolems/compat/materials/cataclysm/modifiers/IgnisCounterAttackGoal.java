package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataDispatch;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

/**
 * 烈焰反击的释放序列：先向后撤一步拉开距离，落地后打出一次打击（{@code ignisCounterStrike}），
 * 随后每 2 tick 掀一对震地波。由 {@link IgnisCounterModifier} 在累计受击到位后置位。
 * <p>
 * 中心/朝向/灵魂标记在起手（落地）瞬间锁定，之后不再跟随傀儡。
 */
public class IgnisCounterAttackGoal extends Goal {

	/** 本体 STRIKE：tick31 打击，之后每 2 tick 掀一对震地波。
	 *  起点向傀儡收到 2（本体从 4 起），末端仍到 17，长度不变。 */

	private final AbstractGolemEntity<?, ?> golem;
	private int step = -1;
	private Vec3 center;
	private float yaw;
	private boolean blue;
	private BackstepHelper backstep;
	private boolean inited;

	public IgnisCounterAttackGoal(AbstractGolemEntity<?, ?> golem) {
		this.golem = golem;
	}

	public IgnisCounterAttackGoal withName(String key) {
		return this;
	}

	@Override
	public boolean canUse() {
		return !golem.level().isClientSide() && IgnisCounterModifier.isReady(golem);
	}

	@Override
	public void start() {
		IgnisCounterModifier.clearReady(golem);
		inited = false;
		step = -1;
		center = null;
		roar();
	}

	/**
	 * 起手咆哮：复刻本体 BREAK_THE_SHIELD 在动画 tick 55 的那一下——
	 * 屏幕震动 + FLAME_BURST 音效 + 咆哮粒子，并把 12 格内所有可攻击目标眩晕 60 tick。
	 * 放在后撤步之前，先震住周围再拉开距离反击。
	 */
	private void roar() {
		GolemCataProxy.ignisRoarPulse(golem, CataDispatch.ignisBlue(golem));
		for (LivingEntity e : golem.level().getEntitiesOfClass(LivingEntity.class,
				golem.getBoundingBox().inflate(IgnisCounterModifier.ROAR_RADIUS))) {
			if (!GolemTargetUtil.canTarget(golem, e)) continue;
			GolemCataProxy.inflictStun(golem, e, IgnisCounterModifier.ROAR_STUN_TICKS);
		}
	}

	@Override
	public boolean canContinueToUse() {
		if (!inited || backstep != null) return true;
		return step >= 0 && step <= IgnisCounterModifier.WAVE_END;
	}

	/**
	 * 序列按 tick 推进，必须每 tick 都跑：Mob 隔 tick 只 tick {@code requiresUpdateEveryTick()} 为 true
	 * 的 Goal，不声明的话波形和音效会慢一倍。
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
			begin();
			return;
		}
		if (backstep != null) {
			// 落地后再起手；后撤途中不推进序列
			if (backstep.tick()) {
				backstep = null;
				begin();
			}
			return;
		}
		if (step >= IgnisCounterModifier.WAVE_START && step <= IgnisCounterModifier.WAVE_END && (step - IgnisCounterModifier.WAVE_START) % 2 == 0) {
			int dist = IgnisCounterModifier.WAVE_BASE + (step - IgnisCounterModifier.WAVE_START);
			// 远程模式下把震地波改成向外击退，近战保持本体的「吸向波形内侧」
			GolemCataProxy.ignisCounterWave(golem, center, yaw, dist, blue, golem.isInRangedMode());
		}
		step++;
	}

	private void begin() {
		step = 0;
		center = golem.position();
		// 朝向目标，而不是傀儡自身前方
		yaw = CataArmorHelper.yawToTarget(golem, golem.getTarget());
		CataArmorHelper.faceYaw(golem, yaw);
		blue = CataDispatch.ignisBlue(golem);
		golem.getNavigation().stop();
		GolemCataProxy.ignisCounterStrike(golem, blue);
	}

	@Override
	public void stop() {
		if (backstep != null) {
			backstep.cancel();
			backstep = null;
		}
		step = -1;
		center = null;
	}

}
