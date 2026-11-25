package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import com.github.L_Ender.cataclysm.init.ModSounds;
import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class HarbingerDeathBeamAttackGoal extends BaseRangedAttackGoal {

	/**
	 * 起手蓄力 tick：Boss 的 {@code DeathLaserGoal} 在动画 tick 18 才生成死亡光束。
	 * 蓄力期间 {@code performAttack} 返回 false（不消耗冷却），与音爆 {@code SonicAttackGoal} 一致：
	 * 中途目标丢失/被中断只会放弃蓄力、不吞冷却，下一次能重新蓄力发射。
	 * <p>
	 * 蓄力计数放在 {@code performAttack} 里推进（而不是 tick）：只有基类真正要开火时才会调用它，
	 * 这样基类因后撤/busy 而跳过开火帧时蓄力不会空转，也就不会每隔一段就重复播一次蓄力音。
	 */
	private static final int CHARGE_TICKS = 18;
	/** 光束寿命（Boss {@code Death_Laser_Beam_Entity(..., 60, ...)}）。 */
	private static final int BEAM_DURATION = 60;

	@Nullable
	private Entity beam;
	/** 剩余蓄力 tick；-1 = 未在蓄力。 */
	private int chargeTicks = -1;

	public HarbingerDeathBeamAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(HarbingerDeathBeamModifier.COOLDOWN, HarbingerDeathBeamModifier.MIN_RANGE, HarbingerDeathBeamModifier.MAX_RANGE, golem, lv);
	}

	@Override
	public boolean canContinueToUse() {
		return beam != null || super.canContinueToUse();
	}

	@Override
	public void stop() {
		chargeTicks = -1;
		super.stop();
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		// Skills that shorten the beam cooldown bank their reduction here; draining it into the attack
		// timer is what makes the shortened cooldown take effect mid-wait.
		int reduction = HarbingerDeathBeamModifier.takeBeamReduction(golem);
		if (reduction > 0) {
			this.attackTime -= reduction;
		}
		LivingEntity le = golem.getTarget();
		if (beam != null) {
			if (beam.isRemoved()) {
				beam = null;
			} else {
				if (le != null) {
					golem.getLookControl().setLookAt(le, 30, 90);
				}
				GolemCataProxy.updateLaser(golem, beam);
				Vec3 origin = golem.getBeamOrigin();
				beam.setPosRaw(origin.x, origin.y, origin.z);
			}
		}
		super.tick();
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		// 起手：第一次进入蓄力时播一次蓄力音（-1 表示还没开始这次蓄力）
		if (chargeTicks < 0) {
			chargeTicks = CHARGE_TICKS;
			golem.level().playSound(null, golem, ModSounds.HARBINGER_DEATHLASER_PREPARE.get(), SoundSource.HOSTILE, 4.0F, 1.2F);
		}
		if (chargeTicks > 0) {
			chargeTicks--;
			golem.getLookControl().setLookAt(target, 30, 90);
			return false; // 蓄力中：不发射、不扣冷却，基类逐 tick 重试
		}
		chargeTicks = -1;
		beam = GolemCataProxy.addLaserBeam(golem, BEAM_DURATION);
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

	@Override
	protected int getWaitTime() {
		int base = super.getWaitTime();
		return CataArmorHelper.harbingerFinalForm(golem) ? Math.max(1, base / 2) : base;
	}

}
