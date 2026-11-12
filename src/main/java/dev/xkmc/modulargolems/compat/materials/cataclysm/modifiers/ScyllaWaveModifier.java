package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.ICooldownSkill;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * 霆浪锚：受到攻击时进入 {@link #BLOCK_DURATION} tick 的格挡窗口（斯库拉本体的招架），
 * 窗口内所有攻击都被格挡，窗口结束后向目标方向召唤海浪推开目标，并进入冷却。
 * <p>
 * 招架的音效与火花照本体：{@code ZOMBIE_ATTACK_IRON_DOOR} + 一圈火花。
 * <p>
 * 格挡窗口的开始与结束都在本类完成；窗口结束时把「待放海浪」标记置上，交给
 * {@link ScyllaWaveAttackGoal} 打出两段海浪。
 */
public class ScyllaWaveModifier extends GolemModifier implements ICooldownSkill {

	/** 格挡窗口时长：2 秒。 */
	public static final int BLOCK_DURATION = 40;
	/** 海浪释放后的冷却。 */
	public static final int PARRY_COOLDOWN = 160;
	/** 招架音效/火花的防抖间隔（只压表现，不影响格挡本身）。 */
	private static final int PARRY_FX_COOLDOWN = 10;
	private static final String KEY_BLOCK_UNTIL = "ScyllaBlockUntil";
	private static final String KEY_COOLDOWN = "ScyllaParryCd";
	private static final String KEY_READY = "ScyllaWaveReady";
	private static final String KEY_FX_LAST = "ScyllaParryFx";

	public ScyllaWaveModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(1, new ScyllaWaveAttackGoal(entity).withName(this.getDescriptionId()));
	}

	@Override
	public void onAttacked(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		if (entity.level().isClientSide()) return;
		var src = event.getSource();
		// 环境伤害 / 无视无敌的伤害不招架
		if (src.getEntity() == null) return;
		if (src.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;
		var data = entity.getPersistentData();
		long time = entity.level().getGameTime();
		// 海浪释放后的冷却期内正常受伤，避免无限格挡
		if (time < data.getLong(KEY_COOLDOWN)) return;
		// 不在格挡窗口内：本次受击开启窗口（这一下也照样格挡）
		if (data.getLong(KEY_BLOCK_UNTIL) <= time) {
			data.putLong(KEY_BLOCK_UNTIL, time + BLOCK_DURATION);
		}
		// 窗口内：格挡本次攻击
		event.setCanceled(true);
		// 音效与 sparks 做防抖，避免被围殴时刷屏
		if (time - data.getLong(KEY_FX_LAST) < PARRY_FX_COOLDOWN) return;
		data.putLong(KEY_FX_LAST, time);
		var level0 = entity.level();
		level0.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
				SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundSource.HOSTILE, 1.0F, 0.8F);
		GolemCataProxy.scyllaParryParticle(entity);
	}

	/**
	 * 格挡窗口的结束由每 tick 的 AI 步骤驱动：到期就置「待放海浪」标记并进入冷却。
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
		long until = golem.getPersistentData().getLong(KEY_COOLDOWN);
		return (int) Math.max(0, until - golem.level().getGameTime());
	}

	@Override
	public String getCooldownNameKey() {
		return getDescriptionId();
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc",
						CataArmorHelper.number(BLOCK_DURATION / 20))
				.withStyle(ChatFormatting.GREEN));
	}

}
