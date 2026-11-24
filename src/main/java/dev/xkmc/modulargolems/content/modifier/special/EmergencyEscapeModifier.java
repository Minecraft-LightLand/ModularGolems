package dev.xkmc.modulargolems.content.modifier.special;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.upgrade.IUpgradeItem;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * 紧急避险（比较器升级）：生命值低于阈值时高亮自身，{@link #WARN_TICKS} 后传送至所有者身边。
 * <ul>
 *   <li>阈值随等级线性提升，每级 10%，封顶 90%（9 级）。</li>
 *   <li>高亮后固定 5 秒传送，冷却 1 分钟。</li>
 *   <li>仅首个升级消耗升级槽：见 {@link #addSlot}。</li>
 * </ul>
 */
public class EmergencyEscapeModifier extends GolemModifier implements ICooldownSkill {

	/** 每级提升的触发血量占比（每级 10%）。 */
	public static final float THRESHOLD_PER_LEVEL = 0.10F;
	/** 触发阈值上限（90%）。 */
	public static final float MAX_THRESHOLD = 0.90F;
	/** 高亮到传送之间的等待时长：5 秒。 */
	public static final int WARN_TICKS = 100;
	/** 冷却：1 分钟。 */
	public static final int COOLDOWN = 1200;

	private static final String KEY_WARN_AT = "EmergencyEscapeWarnAt";
	private static final String KEY_COOLDOWN = "EmergencyEscapeCd";

	public EmergencyEscapeModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	/** 当前等级对应的触发血量占比。 */
	public static float threshold(int level) {
		return Math.min(MAX_THRESHOLD, THRESHOLD_PER_LEVEL * level);
	}

	@Override
	public void onAiStep(AbstractGolemEntity<?, ?> golem, int level) {
		var data = golem.getPersistentData();
		long time = golem.level().getGameTime();
		long warnAt = data.getLong(KEY_WARN_AT);
		if (warnAt > 0) {
			// 已高亮：每秒响一次音符盒倒计时，满 5 秒后传送
			long remaining = warnAt - time;
			if (remaining > 0) {
				if (remaining % 20 == 0) {
					playCountdown(golem, (int) ((WARN_TICKS - remaining) / 20));
				}
				return;
			}
			data.putLong(KEY_WARN_AT, 0);
			golem.setGlowingTag(false);
			if (teleportToOwner(golem)) {
				data.putLong(KEY_COOLDOWN, time + COOLDOWN);
			}
			return;
		}
		// 没有待触发的窗口时清掉可能残留的高亮（例如中途被卸下升级）
		if (golem.hasGlowingTag()) golem.setGlowingTag(false);
		if (time < data.getLong(KEY_COOLDOWN)) return;
		if (!(golem.getOwner() instanceof Player)) return;
		if (golem.getHealth() >= golem.getMaxHealth() * threshold(level)) return;
		// 触发：高亮自身，倒计时传送
		data.putLong(KEY_WARN_AT, time + WARN_TICKS);
		golem.setGlowingTag(true);
		playCountdown(golem, 0);
	}

	/** 倒计时音符：每秒一声，音调逐步升高（第 0 声最低，越接近传送越尖）。 */
	private static void playCountdown(AbstractGolemEntity<?, ?> golem, int step) {
		float pitch = 1.0F + 0.15F * step;
		golem.level().playSound(null, golem.getX(), golem.getY(), golem.getZ(),
				SoundEvents.NOTE_BLOCK_PLING.value(), golem.getSoundSource(), 1.0F, pitch);
	}

	private static boolean teleportToOwner(AbstractGolemEntity<?, ?> golem) {
		Player owner = golem.getOwner();
		if (owner == null || owner.level() != golem.level()) return false;
		golem.level().playSound(null, golem.getX(), golem.getY(), golem.getZ(),
				SoundEvents.ENDERMAN_TELEPORT, golem.getSoundSource(), 1.0F, 1.0F);
		boolean ok = golem.randomTeleport(owner.getX(), owner.getY(), owner.getZ(), true);
		if (ok) {
			golem.getNavigation().stop();
		}
		return ok;
	}

	/**
	 * 仅首个升级消耗升级槽：每个副本照常扣 1 槽（{@link IUpgradeItem#consumesSlot()}），
	 * 这里按等级返还 {@code lv - 1} 个槽，使净消耗恒为 1。
	 */
	@Override
	public int addSlot(List<IUpgradeItem> upgrades, int lv) {
		return Math.max(0, lv - 1);
	}

	@Override
	public int getRemainingCooldown(AbstractGolemEntity<?, ?> golem) {
		long time = golem.level().getGameTime();
		return (int) Math.max(0, golem.getPersistentData().getLong(KEY_COOLDOWN) - time);
	}

	@Override
	public String getCooldownNameKey() {
		return getDescriptionId();
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		var pct = Component.literal("" + Math.round(threshold(v) * 100) + "%").withStyle(ChatFormatting.AQUA);
		var sec = Component.literal("" + (WARN_TICKS / 20)).withStyle(ChatFormatting.AQUA);
		return List.of(Component.translatable(getDescriptionId() + ".desc", pct, sec).withStyle(ChatFormatting.GREEN));
	}

}
