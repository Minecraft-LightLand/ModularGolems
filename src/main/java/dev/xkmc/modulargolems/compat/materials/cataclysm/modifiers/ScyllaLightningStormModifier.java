package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.l2damagetracker.contents.attack.CreateSourceEvent;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.List;
import java.util.function.BiConsumer;

public class ScyllaLightningStormModifier extends GolemModifier {

	public static final int COOLDOWN = 200;
	public static final int MAX_RANGE = 30;
	public static final int MIN_RANGE = 0;
	public static final int CLOUD_TICKS = 60;
	public static final int SPEAR_TICKS = 30;
	public static final float SERPENT_SPREAD_1 = 8.0F;
	public static final float SERPENT_SPREAD_2 = 12.0F;
	public static final int SERPENT_RING_TICK = 30;
	public static final int SERPENT_ROAR_GAP = 3;
	public static final int SERPENT_ROAR_COUNT = 3;
	public static final int SERPENT_PAIR = 2;
	public static final int SERPENT_ORB_END = 40;
	public static final double STORM_KNOCKBACK = 0.7;
	public static final double STORM_KNOCKBACK_RADIUS = 5.5;
	public static final double STORM_KNOCKBACK_MIN_DIST = 1.0;

	public ScyllaLightningStormModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new ScyllaLightningStormAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

	@Override
	public void modifySource(AbstractGolemEntity<?, ?> golem, CreateSourceEvent event, int value) {
		ScyllaInvulnerabilityBypass.onSerpentSource(golem, event);
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
