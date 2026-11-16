package dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * 浮云傀儡的生命值分阶段判定。
 * <p>
 * Boss 的二阶段从 {@code Crackiness.MEDIUM}（低于 65%）开始：tick() 里一旦裂痕等级到 MEDIUM
 * 就置 attackLock = 1，随后切进 state21 变身 angry。所以傀儡也按 65%，而不是 MG 灾变护甲
 * 惯用的半血。
 */
public class CloudPhaseHelper {

	/** 二阶段阈值：低于该比例进入二阶段（Boss 的 Crackiness.MEDIUM）。 */
	public static final float PHASE_TWO_HEALTH = 0.65F;

	/** 是否已进入二阶段（低血量）。 */
	public static boolean inSecondPhase(AbstractGolemEntity<?, ?> golem) {
		// Boss 的 byFraction 是严格小于：恰好 65% 仍算一阶段（Crackiness.LOW）
		return golem.getHealth() < golem.getMaxHealth() * PHASE_TWO_HEALTH;
	}

	/** 是否仍在一阶段（高血量）。 */
	public static boolean inFirstPhase(AbstractGolemEntity<?, ?> golem) {
		return !inSecondPhase(golem);
	}

	/** tooltip 里显示的阈值百分比（AQUA），与技能栏其它数字一致。 */
	public static MutableComponent healthPct() {
		return Component.literal(Math.round(PHASE_TWO_HEALTH * 100) + "%").withStyle(ChatFormatting.AQUA);
	}

	/** 二阶段技能的「仅在低于 65% 可用」提示行。 */
	public static MutableComponent secondPhaseGate() {
		return MGLangData.SKILL_HEALTH_GATE.get(healthPct());
	}

	/** 一阶段技能的「仅在高于 65% 可用」提示行。 */
	public static MutableComponent firstPhaseGate() {
		return MGLangData.SKILL_HEALTH_GATE_ABOVE.get(healthPct());
	}

}
