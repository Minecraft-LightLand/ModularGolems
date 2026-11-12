package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataDispatch;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.util.GolemCombatHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import com.github.L_Ender.cataclysm.init.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.function.BiConsumer;

public class IgnisChargeModifier extends GolemModifier implements ChargeHelper.Modifier {
	public static final int COOLDOWN = 200;

	public static final int MAX_TICKS = 60;
	public static final double MAX_RANGE = 16;
	/** 冲锋的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;
	/** 本体 BodyCheckAttack 普通形态眩晕 tick。 */
	public static final int STUN_TICKS = 80;
	/** 本体 BodyCheckAttack 魂形态（半血/胸甲）眩晕 tick。 */
	public static final int STUN_TICKS_FINAL = 100;

	public IgnisChargeModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	/** 命中眩晕：半血（或穿胸甲）时用魂形态的 100 tick。 */
	public static int stunTicks(AbstractGolemEntity<?, ?> golem) {
		return CataArmorHelper.ignisFinalForm(golem) ? STUN_TICKS_FINAL : STUN_TICKS;
	}

	@Override
	public int getCooldown(AbstractGolemEntity<?, ?> golem, int lv) {
		return COOLDOWN;
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(1, new IgnisChargeAttackGoal(entity, lv, this));
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, LivingDamageEvent event, int level) {
		if (IgnisChargeAttackGoal.isCharging(entity)) {
			event.setAmount(event.getAmount() * ChargeHelper.CHARGE_DAMAGE_MULT);
		}
	}

	@Override
	public void setCharging(ChargeHelper.Instance ins, boolean on) {
		ins.owner().setTransientFlag(IgnisChargeAttackGoal.KEY_ACTIVE, on);
	}

	/**
	 * 冲锋起手音效：本体焰魔的 FLAME_BURST（原先放在火球发射里的那个）。
	 * <p>
	 * 用 {@code playSound(null, ...)} 由服务端广播——{@code playLocalSound} 只在客户端生效，
	 * 而技能 Goal 只在服务端跑，用它的话玩家什么都听不到。
	 */
	public static void playChargeSound(LivingEntity user) {
		user.level().playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.FLAME_BURST.get(),
				SoundSource.PLAYERS, 1.0F, 1.0F);
	}

	@Override
	public void performCharge(ChargeHelper.Instance ins) {
		playChargeSound(ins.owner());
	}

	@Override
	public ParticleOptions getChargeParticle() {
		return ParticleTypes.FLAME;
	}

	/**
	 * 骑乘冲锋的拖尾开关。默认 {@code false}，只有 AI 冲锋的 Goal 里自己调
	 * {@code spawnChargeParticles}，所以骑乘时完全没有火焰拖尾——这里打开，
	 * 由 {@code MountChargeManager} 每 3 tick 喷一次。
	 */
	@Override
	public boolean hasChargeParticle() {
		return true;
	}

	@Override
	public float chargeDamage(ChargeHelper.Instance ins, LivingEntity target) {
		float atk = GolemCombatHelper.getAttackDamage(ins.owner());
		if (CataDispatch.ignisBlue(ins.owner())) atk *= 4f / 3;
		float dmg = atk * 1.1f + target.getMaxHealth() * 0.05f;
		// 腾炎护腿：狂焰冲刺伤害提高
		if (CataArmorHelper.ignisLegs(ins.owner()))
			dmg *= 1f + MGConfig.COMMON.flameStrikeArmorBonus.get().floatValue();
		return dmg;
	}

	@Override
	public void onChargeTouch(ChargeHelper.Instance ins, LivingEntity target) {
		// 命中音效统一在 MountChargeManager 播，这里只保留眩晕，避免骑乘冲锋时响两声
		GolemCataProxy.inflictStun(ins.owner(), target, stunTicks(ins.owner()));
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc",
				ChargeHelper.chargeReduction(),
				CataArmorHelper.healthPct(CataArmorHelper.TWO_THIRDS),
				CataArmorHelper.number((STUN_TICKS_FINAL - STUN_TICKS) / 20)).withStyle(ChatFormatting.GREEN));
	}

}
