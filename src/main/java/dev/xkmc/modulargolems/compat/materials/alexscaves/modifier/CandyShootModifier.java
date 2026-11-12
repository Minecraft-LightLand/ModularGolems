package dev.xkmc.modulargolems.compat.materials.alexscaves.modifier;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.List;
import java.util.function.BiConsumer;

public class CandyShootModifier extends GolemModifier {

	/** 冷却（tick）。 */
	public static final int COOLDOWN = 20;
	/** 施放的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;
	/** 施放的最大距离（格）。 */
	public static final int MAX_RANGE = 24;

	public CandyShootModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(2, new GumShootAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		int num = (int) Math.round(MGConfig.COMMON.candyDamage.get() * v);
		var val = Component.literal("" + num).withStyle(ChatFormatting.AQUA);
		return List.of(Component.translatable(getDescriptionId() + ".desc", val).withStyle(ChatFormatting.GREEN));
	}

}
