package dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.ICooldownSkill;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;

/**
 * 浮云格挡（照 {@code ScyllaWaveModifier} / 格挡反击的结构，但<b>不做完全格挡</b>）：
 * 受击时进入 {@link #BLOCK_DURATION} tick 的格挡窗口，窗口内<b>单次受到的伤害封顶 {@link #DAMAGE_CAP}</b>，
 * 窗口结束后进入冷却。
 * <p>
 * 对应 Cloud_Golem 手臂重击（state 18/19）期间的破盾减伤：Boss 在 {@code canBreak()} 时把
 * 单次 &gt;5 的伤害削到 4，直到被斧头破防。这里简化为"受击开窗口 + 窗口内限伤"，不做破防计数。
 */
public class CloudBlockModifier extends GolemModifier implements ICooldownSkill {

	/** 格挡窗口时长（tick）：2 秒。 */
	public static final int BLOCK_DURATION = 40;
	/** 格挡释放后的冷却（tick）。 */
	public static final int PARRY_COOLDOWN = 160;
	/** 窗口内单次受到伤害的上限（照 Boss 的 4 点）。 */
	public static final float DAMAGE_CAP = 4.0F;
	/** 格挡音效/特效防抖（只压表现，不影响减伤）。 */
	private static final int FX_COOLDOWN = 10;

	private static final String KEY_BLOCK_UNTIL = "CloudBlockUntil";
	private static final String KEY_COOLDOWN = "CloudBlockCd";
	private static final String KEY_FX_LAST = "CloudBlockFx";

	public CloudBlockModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
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
		// 冷却期内不格挡
		if (time < data.getLong(KEY_COOLDOWN)) return;
		// 不在窗口内：本次受击开启窗口
		if (data.getLong(KEY_BLOCK_UNTIL) <= time) {
			data.putLong(KEY_BLOCK_UNTIL, time + BLOCK_DURATION);
		}
		// 音效/特效防抖（限伤在 onHurt 做）
		if (time - data.getLong(KEY_FX_LAST) < FX_COOLDOWN) return;
		data.putLong(KEY_FX_LAST, time);
		LMProxy.cloudBlockFx(entity);
	}

	@Override
	public void onHurt(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {
		if (entity.level().isClientSide()) return;
		var data = entity.getPersistentData();
		long time = entity.level().getGameTime();
		// 仅在格挡窗口内限伤
		if (data.getLong(KEY_BLOCK_UNTIL) > time && event.getAmount() > DAMAGE_CAP) {
			event.setAmount(DAMAGE_CAP);
		}
	}

	/** 窗口结束由每 tick 的 AI 步骤驱动：到期就进入冷却（玩家不打它时窗口也要正常结束）。 */
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
		return List.of(Component.translatable(getDescriptionId() + ".desc",
				Component.literal("" + BLOCK_DURATION / 20).withStyle(ChatFormatting.AQUA),
				Component.literal("" + (int) DAMAGE_CAP).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GREEN));
	}

}
