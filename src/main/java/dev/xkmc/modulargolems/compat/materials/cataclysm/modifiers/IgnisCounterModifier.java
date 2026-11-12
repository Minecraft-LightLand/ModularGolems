package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.ICooldownSkill;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * 烈焰反击：窗口期内累计受到足够次数的攻击后，向前方释放一道扇形冲击波。
 * <p>
 * 计数在 {@code onAttacked} 里完成；累计到位后把一个「待反击」标记交给 {@link IgnisCounterAttackGoal}，
 * 由它在多帧里打出打击 + 扇形震地波。反击序列的状态放在 Goal 实例里，不用实体的临时状态。
 */
public class IgnisCounterModifier extends GolemModifier implements ICooldownSkill {

	public static final int WAVE_START = 5;
	public static final int WAVE_END = 19;
	public static final int WAVE_BASE = 2;

	/** 起手咆哮的作用半径（格）；本体 BREAK_THE_SHIELD tick55 用 12。 */
	public static final int ROAR_RADIUS = 12;
	/** 起手咆哮的眩晕 tick；本体 tick55 用 60。 */
	public static final int ROAR_STUN_TICKS = 60;

	/** 连续窗口：超过这么久没再挨打就重新计数。 */
	private static final int WINDOW_TICKS = 200;
	/** 需要累计的受击次数。 */
	private static final int REQUIRED_HITS = 5;
	/** 同一次连击里的防抖，避免同帧多次伤害重复计数。 */
	private static final int HIT_DEBOUNCE = 5;
	/** 反击后的冷却。 */
	private static final int COOLDOWN_TICKS = 200;

	private static final String KEY_COUNT = "IgnisCounterHit";
	private static final String KEY_LAST = "IgnisCounterLast";
	private static final String KEY_CD = "IgnisCounterCd";
	private static final String KEY_READY = "IgnisCounterReady";

	public IgnisCounterModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(1, new IgnisCounterAttackGoal(entity).withName(this.getDescriptionId()));
	}

	@Override
	public void onAttacked(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		if (entity.level().isClientSide()) return;
		var src = event.getSource();
		// 环境伤害 / 无视无敌伤害不计入
		if (src.getEntity() == null) return;
		if (src.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;
		// 近战技能：远程模式不触发
		var data = entity.getPersistentData();
		long time = entity.level().getGameTime();
		if (time < data.getLong(KEY_CD)) return;
		long last = data.getLong(KEY_LAST);
		if (last > 0 && time - last < HIT_DEBOUNCE) return;
		int count = last > 0 && time - last > WINDOW_TICKS ? 0 : data.getInt(KEY_COUNT);
		count++;
		data.putLong(KEY_LAST, time);
		if (count < REQUIRED_HITS) {
			data.putInt(KEY_COUNT, count);
			return;
		}
		data.putInt(KEY_COUNT, 0);
		data.putLong(KEY_CD, time + COOLDOWN_TICKS);
		// 交给 Goal 在下一 tick 起手，序列开始后不再跟随傀儡
		data.putBoolean(KEY_READY, true);
	}

	/** Goal 检查是否有待反击的请求。 */
	static boolean isReady(AbstractGolemEntity<?, ?> golem) {
		return golem.getPersistentData().getBoolean(KEY_READY);
	}

	/** Goal 起手时清掉请求标记。 */
	static void clearReady(AbstractGolemEntity<?, ?> golem) {
		golem.getPersistentData().putBoolean(KEY_READY, false);
	}

	@Override
	public int getRemainingCooldown(AbstractGolemEntity<?, ?> golem) {
		long until = golem.getPersistentData().getLong(KEY_CD);
		return (int) Math.max(0, until - golem.level().getGameTime());
	}

	@Override
	public String getCooldownNameKey() {
		return getDescriptionId();
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc",
				CataArmorHelper.number(WINDOW_TICKS / 20),
				CataArmorHelper.number(REQUIRED_HITS)).withStyle(ChatFormatting.GREEN));
	}

}
