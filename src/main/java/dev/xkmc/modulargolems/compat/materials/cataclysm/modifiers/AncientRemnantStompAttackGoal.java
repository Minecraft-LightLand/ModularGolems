package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * 裂地重踏：重踏地面，向目标方向逐圈推出一道擦地冲击波。
 * <p>
 * 复刻灾变远古遗魂的招式 attackState 8：本体在 attackTicks 28/30/32/…/46 每 2 tick 推进一次，
 * 每次在 distance 与 distance+1 两处各造成一道 0.4π 的擦地冲击，距离从 2 一路收到 19；
 * 起手附带一次碎块尘土爆发与重踏音效。
 * <p>
 * 与远古金属其它技能一致：半血（或穿胸甲）时连踏 {@link AncientRemnantStompModifier#REPEAT} 次。
 * 每踏的起点与朝向在起手时锁定，之后不跟随傀儡。
 */
public class AncientRemnantStompAttackGoal extends BaseRangedAttackGoal {

	/** 本体每 2 tick 推进一次。 */
	private static final int STEP_INTERVAL = 2;

	/** 当前推进到第几步，-1 表示本次施法尚未开始。 */
	private int step = -1;
	/** 本次施法已经踏完几遍。 */
	private int pass;
	/** 本次施法总共要踏几遍。 */
	private int passes = 1;
	private int lastStepTick;
	private Vec3 center;
	private float yaw;

	public AncientRemnantStompAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(AncientRemnantStompModifier.COOLDOWN, AncientRemnantStompModifier.MIN_RANGE, AncientRemnantStompModifier.MAX_RANGE, golem, lv);
	}

	@Override
	public boolean canUse() {
		// 裂地重踏要踩在地面上，空中释放会踏空（与扫戟扇形阵一致）
		if (!golem.onGround()) return false;
		return super.canUse();
	}

	/**
	 * 每次调用推进一段；本体那种每 2 tick 一次的节奏靠返回 false 让基类继续调用实现。
	 */
	@Override
	protected boolean performAttack(LivingEntity target) {
		if (step < 0) {
			passes = CataArmorHelper.ancientFastSkill(golem) ? AncientRemnantStompModifier.REPEAT : 1;
			pass = 0;
			beginPass(target);
			return false;
		}
		if (golem.tickCount - lastStepTick < STEP_INTERVAL) return false;
		step++;
		lastStepTick = golem.tickCount;
		if (step >= AncientRemnantStompModifier.DISTANCES.length) {
			pass++;
			if (pass >= passes) {
				step = -1;
				return true;
			}
			// 连续重踏：重新对准目标再踏一遍
			beginPass(target);
			return false;
		}
		stompOne(step);
		return false;
	}

	private void beginPass(LivingEntity target) {
		AncientRemnantChargeModifier.addChargeReduction(golem);
		center = golem.position();
		// 朝向目标，而不是傀儡自身前方
		yaw = CataArmorHelper.yawToTarget(golem, target);
		CataArmorHelper.faceYaw(golem, yaw);
		golem.getNavigation().stop();
		GolemCataProxy.ancientRemnantStompStart(golem, center, yaw);
		step = 0;
		lastStepTick = golem.tickCount;
		stompOne(0);
	}

	private void stompOne(int index) {
		// 远程模式下把震地改成向外击退，近战保持本体行为
		GolemCataProxy.ancientRemnantStomp(golem, center, yaw, AncientRemnantStompModifier.DISTANCES[index],
				e -> GolemTargetUtil.canTarget(golem, e), golem.isInRangedMode());
	}

	@Override
	protected int getWaitTime() {
		// 远古金属胸甲：半血以下（或被胸甲钉在最终形态）时冷却减半
		int base = super.getWaitTime();
		return CataArmorHelper.ancientFastSkill(golem) ? Math.max(1, base / 2) : base;
	}

	@Override
	public void stop() {
		super.stop();
		// 中途被打断时清掉进度，下次从第一段重新开始
		step = -1;
		pass = 0;
		passes = 1;
		center = null;
	}

}
