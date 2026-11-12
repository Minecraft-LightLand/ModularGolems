package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.List;
import java.util.function.BiConsumer;

public class ScyllaWaterBoltModifier extends GolemModifier {
	public static final int COOLDOWN = 200;
	/** 施放距离上限（格）。 */
	public static final int MAX_RANGE = 35;
	/** 施放的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;
	/** 满血时的齐射数量。 */
	public static final int BURST_COUNT = 1;
	/** 半血（或穿风暴胸甲）时的齐射数量。 */
	public static final int BURST_COUNT_FINAL = 3;

	/** 实际齐射数量：半血（或穿风暴胸甲）时用 3 发。 */
	public static int burstCount(AbstractGolemEntity<?, ?> golem) {
		return CataArmorHelper.stormFinalForm(golem) ? BURST_COUNT_FINAL : BURST_COUNT;
	}

	public ScyllaWaterBoltModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new ScyllaWaterBoltAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

	@Override
	public void onAttackTarget(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		ScyllaInvulnerabilityBypass.onAttackTarget(entity, event, level);
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc",
				CataArmorHelper.number(BURST_COUNT),
				CataArmorHelper.healthPct(CataArmorHelper.TWO_THIRDS),
				CataArmorHelper.number(BURST_COUNT_FINAL)).withStyle(ChatFormatting.GREEN));
	}


	/** 冷却随等级右移（每级减半），最低 1 tick，永不归零。 */
	public static int cooldownFor(int lv) {
		return Math.max(1, COOLDOWN >> (lv - 1));
	}

}
