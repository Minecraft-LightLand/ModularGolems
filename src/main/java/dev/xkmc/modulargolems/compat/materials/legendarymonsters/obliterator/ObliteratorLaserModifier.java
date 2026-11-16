package dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

/**
 * Single-target laser modifier (Obliterator).
 * Proxy spawns one AnnihilationBeamEntity per attack.
 * Reference: TheObliteratorEntity states 42/43 single shot laser (single proxy) vs 46 quad (multi)
 */
public class ObliteratorLaserModifier extends GolemModifier {

	/** 冷却（tick）。 */
	public static final int COOLDOWN = 200;
	/** 施放距离上限（格）。 */
	public static final int MAX_RANGE = 32;
	/** 施放的最小距离（格）；2 = 太近就不放。 */
	public static final int MIN_RANGE = 2;
	/** 二阶段开火后衔接四向激光的概率，对应 Boss state46 的派生。 */
	public static final float QUAD_CHANCE = 0.6F;

	public ObliteratorLaserModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new ObliteratorLaserAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}
}
