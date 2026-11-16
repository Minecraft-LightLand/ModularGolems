package dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * 克隆八向炮（对应 Boss 的 AmbushGoal / state13/14/62）：
 * 以目标为中心克隆 8 个虚影环绕目标，同时发射湮灭炮弹。
 * 二阶段（生命 &lt;65%）起再追加一轮，与 Boss 的 phase&gt;=2 分支一致。
 */
public class ObliteratorCloneBarrageModifier extends GolemModifier {

	public static final int COOLDOWN = 240;
	/** 施放距离上限（格）。 */
	public static final int MAX_RANGE = 20;
	/** 施放的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;

	public ObliteratorCloneBarrageModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new ObliteratorCloneBarrageAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(
				Component.translatable(getDescriptionId() + ".desc").withStyle(ChatFormatting.GREEN),
				ObliteratorPhaseHelper.phaseTwoGate().withStyle(ChatFormatting.GREEN));
	}

}
