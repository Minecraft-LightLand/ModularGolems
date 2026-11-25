package dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor.LMArmorHelper;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.ICooldownSkill;
import dev.xkmc.modulargolems.util.SkillSound;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;

import java.util.List;

/**
 * 浮云格挡（结构照霆浪锚 {@code ScyllaWaveModifier}）：受击时进入 {@link #BLOCK_DURATION} tick 的格挡窗口。
 * <ul>
 *   <li>穿<b>浮云胸甲</b>：窗口内完全格挡攻击（{@code event.setCanceled(true)}，连击退/受击判定都挡掉），
 *       窗口结束时向四周召唤全方向烈波（伤害无视无敌帧且追踪）。</li>
 *   <li>未穿胸甲：窗口内单次受到的伤害封顶 {@link #DAMAGE_CAP}（照 Boss {@code canBreak()} 的 4 点）。</li>
 * </ul>
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

	/**
	 * 穿浮云胸甲时在「受击判定」阶段就取消整次攻击（照 1.20 的 {@code setCanceled}）：
	 * 只把最终伤害乘 0 挡不住 invulnerableTime / hurtTime / 击退 / lastHurt，会表现为"格挡了还在挨打"。
	 * 未穿胸甲时不取消，交给 {@link #onDamaged} 做单次限伤。
	 */
	@Override
	public boolean onAttacked(AbstractGolemEntity<?, ?> entity, DamageData.Attack event, int level) {
		if (entity.level().isClientSide()) return false;
		if (!LMArmorHelper.cloudChest(entity)) return false;
		var data = entity.getPersistentData();
		long time = entity.level().getGameTime();
		var src = event.getSource();
		if (src.getEntity() == null) return false;
		if (src.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return false;
		if (time < data.getLong(KEY_COOLDOWN)) return false;
		if (data.getLong(KEY_BLOCK_UNTIL) <= time) {
			data.putLong(KEY_BLOCK_UNTIL, time + BLOCK_DURATION);
		}
		if (time - data.getLong(KEY_FX_LAST) >= FX_COOLDOWN) {
			data.putLong(KEY_FX_LAST, time);
			LMProxy.cloudBlockFx(entity);
		}
		return true;
	}

	/** 未穿浮云胸甲时：窗口内单次受到的伤害封顶（{@link #DAMAGE_CAP}）。 */
	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, DamageData.Defence event, int level) {
		if (entity.level().isClientSide()) return;
		if (LMArmorHelper.cloudChest(entity)) return;
		var data = entity.getPersistentData();
		long time = entity.level().getGameTime();
		if (data.getLong(KEY_BLOCK_UNTIL) <= time) return;
		if (event.getDamageOriginal() > DAMAGE_CAP) {
			event.addDealtModifier(DamageModifier.multTotal(DAMAGE_CAP / event.getDamageOriginal(), getRegistryName()));
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
		// 浮云胸甲：格挡窗口结束时向四周召唤一圈全方向烈波（音效同烈波重踏）
		if (LMArmorHelper.cloudChest(golem)) {
			SkillSound.playQuakeOnly(golem, SoundEvents.DRAGON_FIREBALL_EXPLODE, 3.0F, 1.0F);
			LMProxy.spawnCloudGolemWaves(golem, 12, 30.0f, LMProxy.bossDamage(golem, level, 7.0f),
					45.0f, -1.0, golem.getX(), golem.getZ(), 0.0f);
		}
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
