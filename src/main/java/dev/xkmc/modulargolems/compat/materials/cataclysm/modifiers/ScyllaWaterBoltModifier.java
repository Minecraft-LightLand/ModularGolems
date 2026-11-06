package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.List;
import java.util.function.BiConsumer;

public class ScyllaWaterBoltModifier extends GolemModifier {

	public ScyllaWaterBoltModifier() {
		super(StatFilterType.ATTACK, 3);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new ScyllaWaterBoltGoal(entity, lv).withName(this.getDescriptionId()));
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		// 齐射数量随生命值变化，文本里 %s 表示上限（满血 1 发，2/3 血以下 3 发）。
		return List.of(Component.translatable(getDescriptionId() + ".desc", "3").withStyle(ChatFormatting.GREEN));
	}

}
