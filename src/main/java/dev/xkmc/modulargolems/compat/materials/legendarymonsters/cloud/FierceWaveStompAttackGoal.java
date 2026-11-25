package dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor.LMArmorHelper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import dev.xkmc.modulargolems.util.SkillSound;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

/**
 * 烈波重踏：照 Cloud_Golem state25 的节奏 —— 起手后 tick 25 第一次踏地（7 道烈波），
 * tick 41 第二次踏地（9 道烈波），整招 56 tick。
 * <p>
 * 每次踏地：{@code DRAGON_FIREBALL_EXPLODE(3, 1)} + 扇形烈波 + 广播客户端表现事件。
 * 没有额外的范围伤害 —— 伤害全部来自烈波本身（{@code ElectricityEntity}，7 点、无视护甲与盾牌）。
 */
public class FierceWaveStompAttackGoal extends BaseRangedAttackGoal {

	private boolean casting;
	private int castTicks;
	private float castYaw;   // 只锁朝向；位置每次用当时脚下（踏步技能）

	public FierceWaveStompAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(FierceWaveStompModifier.COOLDOWN, FierceWaveStompModifier.MIN_RANGE,
				FierceWaveStompModifier.MAX_RANGE, golem, lv);
	}

	/** 二阶段（<65% 生命）才开放，对应 Boss state25 的 isAngry 门槛。 */
	@Override
	public boolean canUse() {
		return LMArmorHelper.cloudFinalForm(golem) && golem.onGround() && super.canUse();
	}

	@Override
	public void start() {
		super.start();
		casting = false;
		castTicks = 0;
	}

	@Override
	public boolean canContinueToUse() {
		return casting || super.canContinueToUse();
	}

	@Override
	public void stop() {
		super.stop();
		casting = false;
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		if (casting) {
			LivingEntity target = golem.getTarget();
			if (target == null || golem.isRemoved()) {
				casting = false;
			} else {
				castTicks++;
				if (castTicks == FierceWaveStompModifier.STOMP_1_TICK) {
					stomp(FierceWaveStompModifier.WAVES_1, FierceWaveStompModifier.STEP_1, -1.0);
				}
				if (castTicks == FierceWaveStompModifier.STOMP_2_TICK) {
					stomp(FierceWaveStompModifier.WAVES_2, FierceWaveStompModifier.STEP_2,
							FierceWaveStompModifier.Y_OFFSET_2);
				}
				if (castTicks >= FierceWaveStompModifier.TOTAL_TICKS) casting = false;
			}
		}
		super.tick();
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		if (casting) return false;
		// 起手锁定朝向，整招不再跟随目标转身（两次踏地共用同一朝向）
		float yaw = (float) (Mth.atan2(-(target.getX() - golem.getX()), target.getZ() - golem.getZ()) * Mth.RAD_TO_DEG);
		castYaw = yaw;
		golem.setYRot(yaw);
		golem.yBodyRot = yaw;
		golem.yHeadRot = yaw;
		castTicks = 0;
		casting = true;
		return true;
	}

	/** 一次踏地：音效 + 扇形烈波 + 客户端表现事件。 */
	private void stomp(int waves, float angleStep, double yOffsetRatio) {
		if (golem.level().isClientSide) return;
		SkillSound.playQuakeOnly(golem, SoundEvents.DRAGON_FIREBALL_EXPLODE, 3.0F, 1.0F);
		LMProxy.spawnCloudGolemWaves(golem, waves, angleStep,
				FierceWaveStompModifier.WAVE_DAMAGE, FierceWaveStompModifier.WAVE_LIFE, yOffsetRatio, golem.getX(), golem.getZ(), castYaw);
		golem.level().broadcastEntityEvent(golem, FierceWaveStompModifier.EVENT_STOMP);
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
