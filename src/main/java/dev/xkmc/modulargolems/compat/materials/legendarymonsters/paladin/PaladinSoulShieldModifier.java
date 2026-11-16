package dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.ICooldownSkill;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * 格挡反击（对应堕落圣骑 Boss state5 的招架 → 反击）。结构照灾变「霆浪锚」
 * {@code ScyllaWaveModifier}：受击时进入 {@link #BLOCK_DURATION} tick 的格挡窗口，
 * 窗口内所有攻击都被格挡；窗口结束后向目标后撤一步，落地横扫正面（碎魂 + 碎甲 + 眩晕 55），
 * 3 tick 后再投出 6 把灵魂匕首并补一次扇形范围伤害，随后进入冷却。
 * <p>
 * 格挡音效/特效照堕落圣骑 Boss：{@code ModSounds.BLOCK} 1.0 + CameraShake。
 */
public class PaladinSoulShieldModifier extends GolemModifier implements ICooldownSkill {

	/** 格挡窗口时长（tick）：2 秒。 */
	public static final int BLOCK_DURATION = 40;
	/** 盾击释放后的冷却（tick）。 */
	public static final int PARRY_COOLDOWN = 160;
	/** 格挡音效/特效防抖（只压表现，不影响格挡本身）。 */
	private static final int PARRY_FX_COOLDOWN = 10;
	private static final String KEY_BLOCK_UNTIL = "PaladinShieldBlockUntil";
	private static final String KEY_COOLDOWN = "PaladinShieldParryCd";
	private static final String KEY_READY = "PaladinShieldReady";
	private static final String KEY_FX_LAST = "PaladinShieldFx";

	public PaladinSoulShieldModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(1, new PaladinSoulShieldAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

	@Override
	public void onAttacked(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		if (entity.level().isClientSide()) return;
		var src = event.getSource();
		// 环境伤害 / 无视无敌的伤害不格挡
		if (src.getEntity() == null) return;
		if (src.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;
		var data = entity.getPersistentData();
		long time = entity.level().getGameTime();
		// 释放后冷却期内正常受伤，避免无限格挡
		if (time < data.getLong(KEY_COOLDOWN)) return;
		// 不在格挡窗口内：本次受击开启窗口（这一下也照样格挡）
		if (data.getLong(KEY_BLOCK_UNTIL) <= time) {
			data.putLong(KEY_BLOCK_UNTIL, time + BLOCK_DURATION);
		}
		// 窗口内：格挡本次攻击
		event.setCanceled(true);
		if (time - data.getLong(KEY_FX_LAST) < PARRY_FX_COOLDOWN) return;
		data.putLong(KEY_FX_LAST, time);
		LMProxy.paladinBlockFx(entity);
	}

	/**
	 * 窗口结束由每 tick 的 AI 步骤驱动：到期就置「待放盾击」标记并进入冷却。
	 * 不能只靠受击事件——玩家不打它时窗口也要正常结束。
	 */
	@Override
	public void onAiStep(AbstractGolemEntity<?, ?> golem, int level) {
		if (golem.level().isClientSide()) return;
		var data = golem.getPersistentData();
		long until = data.getLong(KEY_BLOCK_UNTIL);
		if (until <= 0) return;
		long time = golem.level().getGameTime();
		if (time < until) return;
		data.putLong(KEY_BLOCK_UNTIL, 0);
		data.putLong(KEY_COOLDOWN, time + PARRY_COOLDOWN);
		data.putBoolean(KEY_READY, true);
	}

	static boolean isReady(AbstractGolemEntity<?, ?> golem) {
		return golem.getPersistentData().getBoolean(KEY_READY);
	}

	/** Goal 起手时清掉请求标记。 */
	static void clearReady(AbstractGolemEntity<?, ?> golem) {
		golem.getPersistentData().putBoolean(KEY_READY, false);
	}

	@Override
	public int getRemainingCooldown(AbstractGolemEntity<?, ?> golem) {
		return (int) Math.max(0, golem.getPersistentData().getLong(KEY_COOLDOWN) - golem.level().getGameTime());
	}

	@Override
	public String getCooldownNameKey() {
		return getDescriptionId();
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc", Component.literal("" + BLOCK_DURATION / 20).withStyle(ChatFormatting.AQUA), Component.literal("6").withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GREEN));
	}

}
