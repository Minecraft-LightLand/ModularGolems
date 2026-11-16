package dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin;

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
 * 灵魂爆破（对应堕落圣骑 Boss state31 的 SoulStrike(1.0F, 15, 0.0F)）：向四周放射 15 道灵魂冲击。
 * 释放方式照「扫戟扇形阵」：一次铺满一圈。
 */
public class PaladinSoulBlastModifier extends GolemModifier {

	public static final int COOLDOWN = 200;
	public static final int MAX_RANGE = 16;
	public static final int MIN_RANGE = 0;

	public PaladinSoulBlastModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new PaladinSoulBlastAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc").withStyle(ChatFormatting.GREEN));
	}

}
