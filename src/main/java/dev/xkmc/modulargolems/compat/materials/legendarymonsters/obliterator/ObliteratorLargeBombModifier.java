package dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor.LMArmorEffects;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor.LMArmorHelper;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.AnnihilationBombEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.SmallAnnihilationBombEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

/**
 * Single-target large bomb modifier (Obliterator).
 * Proxy spawns AnnihilationBombEntity once per attack.
 * Reference: TheObliteratorEntity state 6 / 55 shootAnnihilationBomb
 */
public class ObliteratorLargeBombModifier extends GolemModifier {

	/** 冷却（tick）。 */
	public static final int COOLDOWN = 200;
	/** 施放距离上限（格），对应 Boss 的 attackrange 24。 */
	public static final int MAX_RANGE = 24;
	/** 施放的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;

	/** 每级减半冷却（与湮灭炸弹一致）。 */
	public static int cooldownFor(int lv) {
		return Math.max(1, COOLDOWN >> (lv - 1));
	}

	public ObliteratorLargeBombModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new ObliteratorLargeBombAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

	/** 湮灭头盔：发射出去的湮灭炸弹持续追踪目标（每枚独立分配目标，照遗魂沙暴）。 */
	@Override
	public void onAiStep(AbstractGolemEntity<?, ?> golem, int level) {
		if (golem.level().isClientSide()) return;
		if (!LMArmorHelper.obliteratorHelmet(golem)) return;
		LivingEntity target = golem.getTarget();
		if (target == null || !target.isAlive()) return;
		LMArmorEffects.steer(golem, SmallAnnihilationBombEntity.class, true, e -> e.getOwner() == golem);
		LMArmorEffects.steer(golem, AnnihilationBombEntity.class, true, e -> e.getOwner() == golem);
	}
//  目前用不到，留作备份
//	@Override
//	public boolean canExistOn(GolemPart<?, ?> part) {
//		return part.getEntityType() == GolemTypes.TYPE_GOLEM.get() && super.canExistOn(part);
//	}

}
