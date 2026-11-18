package dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor.LMArmorHelper;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * 克隆爆发（对应 Boss state24）：做成真·冲锋技能（{@link BaseChargeGoal} 体系）。
 * 开局生成 3 只持械虚影，本体随即像其它冲锋技能一样朝目标冲刺，撞击造成臂刃伤害，
 * 冲刺结束补一记臂刃范围斩。仅二阶段（生命 &lt;65%）开放。
 */
public class ObliteratorCloneBurstModifier extends GolemModifier implements ChargeHelper.Modifier {

	public static final int COOLDOWN = 220;
	/** 施放距离上限（格），对应 Boss 的 attackrange 8。 */
	public static final int MAX_RANGE = 8;
	/** 施放的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;

	public ObliteratorCloneBurstModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public int getCooldown(AbstractGolemEntity<?, ?> golem, int lv) {
		return COOLDOWN;
	}


	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		// priority 1：高于近战 Goal，冲锋才能抢到 MOVE/LOOK 控制权（与 Ignis 冲锋一致）
		addGoal.accept(1, new ObliteratorCloneBurstAttackGoal(entity, lv, this));
	}

	/** 骑乘冲锋同样召唤三只持械虚影（AI 冲锋由 Goal.begin 召唤）。 */
	@Override
	public void performCharge(ChargeHelper.Instance ins) {
		var golem = ins.owner();
		if (golem.level().isClientSide()) return;
		LivingEntity target = golem.getGolemController() != null ? golem.getGolemController().getTarget() : golem.getTarget();
		if (target == null || !target.isAlive() || !golem.predicateTarget(target)) target = golem.getTarget();
		if (target == null || !target.isAlive()) return;
		LMProxy.spawnCloneBurst(golem, target, ins.lv());
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(
				Component.translatable(getDescriptionId() + ".desc", ChargeHelper.chargeReduction()).withStyle(ChatFormatting.GREEN),
				LMArmorHelper.phaseTwoGate().withStyle(ChatFormatting.GREEN));
	}

}
