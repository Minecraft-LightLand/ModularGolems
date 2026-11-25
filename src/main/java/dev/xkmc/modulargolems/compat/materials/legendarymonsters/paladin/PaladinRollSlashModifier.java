package dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * 翻滚斩（对应堕落圣骑 Boss state29）：向前翻滚冲刺，沿途留下灵魂柱，
 * 并在冲刺中打出两次 360° 斩。做成真冲锋技能（{@link ChargeHelper.Modifier} 体系）。
 */
public class PaladinRollSlashModifier extends GolemModifier implements ChargeHelper.Modifier {

	public static final int COOLDOWN = 200;
	/** 冲锋距离上限（格）。 */
	public static final int MAX_RANGE = 16;
	/** 冲锋的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;
	/** 冲锋期间每 tick 累加的水平旋转角（度）。 */
	public static final float SPIN_SPEED = 40.0F;
	/** 骑乘冲锋期间与 AI 对应的动作帧（复刻 Boss 时间轴）。 */
	private static final int SLASH1_TICK = 10, SLASH2_TICK = 24, PILLAR_INTERVAL = 3;
	private static final String KEY_TICK = "paladinRollRiddenTicks";
	private static final String KEY_CAST = "paladinRollRiddenYaw";

	public PaladinRollSlashModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public int getCooldown(AbstractGolemEntity<?, ?> golem, int lv) {
		return COOLDOWN;
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		// priority 1：高于近战 Goal，冲锋才能抢到 MOVE/LOOK 控制权
		addGoal.accept(1, new PaladinRollSlashAttackGoal(entity, lv, this));
	}

	/** 骑乘冲锋起手（AI 冲锋由 Goal 自己播）：翻滚音 + 起手粒子 + 记录朝向/计时。 */
	@Override
	public void performCharge(ChargeHelper.Instance ins) {
		var golem = ins.owner();
		LMProxy.paladinRollStart(golem);
		Vec3 look = golem.getLookAngle();
		Vec3 dir = new Vec3(look.x, 0, look.z);
		if (dir.lengthSqr() < 1e-8) dir = new Vec3(0, 0, 1);
		ChargeHelper.spawnChargeParticles(golem, dir.normalize(), ParticleTypes.SOUL);
		float castYaw = (float) (Mth.atan2(-look.x, look.z) * Mth.RAD_TO_DEG);
		golem.getPersistentData().putFloat(KEY_CAST, castYaw);
		golem.getPersistentData().putInt(KEY_TICK, 0);
	}

	/** 骑乘冲锋期间补上 AI 的沿途灵魂柱与两次 360° 斩（否则骑乘时看不到特效）。 */
	@Override
	public void tickCharge(ChargeHelper.Instance ins) {
		var golem = ins.owner();
		if (golem.level().isClientSide()) return;
		int t = golem.getPersistentData().getInt(KEY_TICK) + 1;
		golem.getPersistentData().putInt(KEY_TICK, t);
		float castYaw = golem.getPersistentData().getFloat(KEY_CAST);
		if (t % PILLAR_INTERVAL == 0) LMProxy.paladinRollPillar(golem, ins.lv(), castYaw);
		if (t == SLASH1_TICK || t == SLASH2_TICK) LMProxy.paladinRollSlash(golem, ins.lv());
	}


	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc", ChargeHelper.chargeReduction()).withStyle(ChatFormatting.GREEN));
	}

}
