package dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

/**
 * 控制气旋（对应 Cloud_Golem state 23）：朝目标方向甩出一股气旋，卷起沿途目标。
 * 气旋本体用 Legendary Monsters 的 Tornado，见 {@link CycloneAttackGoal}。
 */
public class CycloneModifier extends GolemModifier {

	public static final int COOLDOWN = 240;
	/** 施放距离上限（格）。 */
	public static final int MAX_RANGE = 20;
	/** 施放的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;

	/** 气旋存活时间（tick），对应 Boss 的 Tornado(..., 120)。 */
	public static final int LIFE = 120;
	/** 气旋伤害系数（乘傀儡攻击力）。 */
	public static final float DAMAGE = 0.8F;

	public CycloneModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new CycloneAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

}
