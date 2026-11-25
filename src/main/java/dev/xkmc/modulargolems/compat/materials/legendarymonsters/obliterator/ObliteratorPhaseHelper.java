package dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * 湮灭构造体的阶段判定。
 * <p>
 * Boss 的阶段由 {@code Crackiness} 驱动（见 TheObliteratorEntity#m_8119_ 与 Second/ThirdPhaseStateGoal）：
 * <ul>
 *   <li>二阶段：{@code Crackiness.MEDIUM}（低于 65%）</li>
 *   <li>三阶段：{@code Crackiness.HIGH}（低于 30%）</li>
 * </ul>
 */
public class ObliteratorPhaseHelper {

	/** 二阶段阈值（Boss 的 Crackiness.MEDIUM）。 */
	public static final float PHASE_TWO = 0.65F;
	/** 三阶段阈值（Boss 的 Crackiness.HIGH）。 */
	public static final float PHASE_THREE = 0.3F;

	public static boolean isPhaseTwo(AbstractGolemEntity<?, ?> golem) {
		return golem.getHealth() < golem.getMaxHealth() * PHASE_TWO;
	}

	public static boolean isPhaseThree(AbstractGolemEntity<?, ?> golem) {
		return golem.getHealth() < golem.getMaxHealth() * PHASE_THREE;
	}

	public static MutableComponent phaseTwoGate() {
		return MGLangData.SKILL_HEALTH_GATE.get(pct(PHASE_TWO));
	}

	public static MutableComponent phaseThreeGate() {
		return MGLangData.SKILL_HEALTH_GATE.get(pct(PHASE_THREE));
	}

	public static MutableComponent phaseTwoAboveGate() {
		return MGLangData.SKILL_HEALTH_GATE_ABOVE.get(pct(PHASE_TWO));
	}

	/** 纯百分比文本（如 30%），供技能描述内联使用。 */
	public static MutableComponent pctText(float f) {
		return pct(f);
	}

	private static MutableComponent pct(float f) {
		return Component.literal(Math.round(f * 100) + "%").withStyle(ChatFormatting.AQUA);
	}

}
