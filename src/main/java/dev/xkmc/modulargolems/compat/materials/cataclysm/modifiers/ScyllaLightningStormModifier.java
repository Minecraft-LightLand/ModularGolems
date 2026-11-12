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
	public static final float SERPENT_SPREAD_1 = 8.0F;
	public static final float SERPENT_SPREAD_2 = 12.0F;

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
				Component.translatable(getDescriptionId() + ".desc").withStyle(ChatFormatting.GREEN),
				MGLangData.SKILL_HEALTH_GATE.get(
						CataArmorHelper.healthPct(CataArmorHelper.TWO_THIRDS))
						.withStyle(ChatFormatting.GREEN));
	}

}
