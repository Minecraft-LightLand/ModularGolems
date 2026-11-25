package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
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

import java.util.List;
import java.util.function.BiConsumer;

/**
 * 霆浪锚：受到攻击时进入 {@link #BLOCK_DURATION} tick 的格挡窗口（斯库拉本体的招架），
 * 窗口内伤害清零；窗口结束后向目标方向召唤两段海浪推开目标，并进入冷却。
 * <p>
 * 1.21 用 DamageData.Attack + 返回 true 取消受击（等价 1.20 的 LivingAttackEvent#setCanceled）。
 * <p>
 * 不要用 {@code addDealtModifier(multTotal(0))}：那只是把最终伤害乘 0，攻击本身仍然发生
 * （invulnerableTime / hurtTime / 击退 / lastHurt 都会走一遍），表现为"格挡了但还是在挨打"。
 */
public class ScyllaWaveModifier extends GolemModifier implements ICooldownSkill {

	/** 格挡窗口时长：2 秒。 */
	public static final int BLOCK_DURATION = 40;
	/** 海浪释放后的冷却。 */
	public static final int PARRY_COOLDOWN = 160;
	/** 招架音效/火花的防抖间隔。 */
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
	public boolean onAttacked(AbstractGolemEntity<?, ?> entity, DamageData.Attack event, int level) {
		if (entity.level().isClientSide()) return false;
		var src = event.getSource();
		if (src.getEntity() == null) return false;
		if (src.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return false;
		var data = entity.getPersistentData();
		long time = entity.level().getGameTime();
		if (time < data.getLong(KEY_COOLDOWN)) return false;
		if (data.getLong(KEY_BLOCK_UNTIL) <= time) {
			data.putLong(KEY_BLOCK_UNTIL, time + BLOCK_DURATION);
		}
		// 窗口内：格挡本次攻击（取消整个受击，连击退/受击判定都挡掉）
		if (time - data.getLong(KEY_FX_LAST) >= PARRY_FX_COOLDOWN) {
			data.putLong(KEY_FX_LAST, time);
			var level0 = entity.level();
			level0.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
					SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundSource.HOSTILE, 1.0F, 0.8F);
			GolemCataProxy.scyllaParryParticle(entity);
		}
		return true;
	}

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
