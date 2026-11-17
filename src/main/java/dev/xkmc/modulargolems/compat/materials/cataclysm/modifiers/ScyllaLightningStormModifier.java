package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.init.data.MGLangData;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.function.BiConsumer;

public class ScyllaLightningStormModifier extends GolemModifier {
	public static final int COOLDOWN = 200;
	/** 施放距离上限（格）。 */
	public static final int MAX_RANGE = 30;
	/** 施放的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;

	public static final int CLOUD_TICKS = 60;
	public static final int SPEAR_TICKS = 30;

	/** 幻影水蛇第一波的横向展开（格），本体 {@code SummonSnake} 的 {@code dis = 8}。 */
	public static final float SERPENT_SPREAD_1 = 8.0F;
	/** 二阶段第二波的横向展开（格），本体 {@code dis = 12}。 */
	public static final float SERPENT_SPREAD_2 = 12.0F;
	/** 蓝色冲击环的 tick（本体 {@code attackTicks == 30}）。 */
	public static final int SERPENT_RING_TICK = 30;
	/** 咆哮声波追加的间隔与次数（本体 30 / 33 / 36 / 39）。 */
	public static final int SERPENT_ROAR_GAP = 3;
	public static final int SERPENT_ROAR_COUNT = 3;
	/** 每一波召唤的水蛇条数：≤2/3 一对，≤1/3 再来一对。 */
	public static final int SERPENT_PAIR = 2;
	/** 冲击环之后雷暴粒子的结束 tick（本体 {@code < 40 && > 30}）。 */
	public static final int SERPENT_ORB_END = 40;

	/** 风暴之怒：把附近目标向外推开的力道（本体 Stormknockback 传 0.7）。 */
	public static final double STORM_KNOCKBACK = 0.7;
	/** 风暴之怒：推开的作用半径（格，本体 Stormknockback 传 5.5）。 */
	public static final double STORM_KNOCKBACK_RADIUS = 5.5;
	/** 推力的距离下限（格）：本体按 d² 反比，贴脸时会趋于无穷，这里压到 1 格。 */
	public static final double STORM_KNOCKBACK_MIN_DIST = 1.0;

	public ScyllaLightningStormModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new ScyllaLightningStormAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

	/**
	 * 风暴胸甲：风暴之怒召唤的幻影水蛇攻击也无视目标无敌帧。
	 */
	@Override
	public void onAttackTarget(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		ScyllaInvulnerabilityBypass.onSerpentAttack(entity, event);
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(
				Component.translatable(getDescriptionId() + ".desc",
						CataArmorHelper.healthPct(CataArmorHelper.TWO_THIRDS),
						CataArmorHelper.number(SERPENT_PAIR),
						CataArmorHelper.healthPct(CataArmorHelper.ONE_THIRD),
						CataArmorHelper.number(SERPENT_PAIR * 2)).withStyle(ChatFormatting.GREEN),
				MGLangData.SKILL_HEALTH_GATE.get(
						CataArmorHelper.healthPct(CataArmorHelper.TWO_THIRDS))
						.withStyle(ChatFormatting.GREEN));
	}

}
