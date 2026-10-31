package dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * 浮云冲锋 Modifier。挂到 cloud 材质，连冲两次，冷却同 Boss chargeCooldown(=220)。
 */
public class CloudChargeModifier extends GolemModifier implements ChargeHelper.Modifier {

	public static final int COOLDOWN = 220;

	public CloudChargeModifier() {
		super(StatFilterType.MOVEMENT, 1);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(1, new CloudChargeGoal(entity, lv, this));
	}

	@Override
	public int getCooldown(AbstractGolemEntity<?, ?> golem, int lv) {
		return COOLDOWN;
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		var dashes = Component.literal("" + CloudChargeGoal.MAX_DASH).withStyle(ChatFormatting.AQUA);
		return List.of(Component.translatable(getDescriptionId() + ".desc", dashes).withStyle(ChatFormatting.GREEN));
	}

}
