package dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor.LMArmorHelper;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;
import java.util.List;

/**
 * 天穹激光（对应 Cloud_Golem state 14）：悬停充能后发射一道持续能量光束。
 * 光束本体由 Legendary Monsters 的 EnergyBeamEntity 负责，见 {@link SkyLaserAttackGoal}。
 */
public class SkyLaserModifier extends GolemModifier {

	public static final int COOLDOWN = 200;
	/** 施放距离上限（格）。 */
	public static final int MAX_RANGE = 30;
	/** 施放的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;

	/** 光束持续时间（tick），对应 Boss 的 EnergyBeamEntity(..., 75, ...)。 */
	public static final int BEAM_DURATION = 75;
	/** 光束伤害系数（乘傀儡攻击力）。 */
	public static final float BEAM_DAMAGE = 1.5F;

	public SkyLaserModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new SkyLaserAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(
				Component.translatable(getDescriptionId() + ".desc").withStyle(ChatFormatting.GREEN),
				LMArmorHelper.phaseTwoGate().withStyle(ChatFormatting.GREEN));
	}
}
