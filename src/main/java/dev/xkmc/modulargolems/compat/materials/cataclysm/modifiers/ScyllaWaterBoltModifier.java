package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.attack.CreateSourceEvent;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.List;
import java.util.function.BiConsumer;

public class ScyllaWaterBoltModifier extends GolemModifier {
	public static final int COOLDOWN = 200;
	public static final int MAX_RANGE = 35;
	public static final int MIN_RANGE = 0;
	public static final int BURST_COUNT = 1;
	public static final int BURST_COUNT_FINAL = 3;

	public static int burstCount(AbstractGolemEntity<?, ?> golem) {
		return CataArmorHelper.stormFinalForm(golem) ? BURST_COUNT_FINAL : BURST_COUNT;
	}

	public static int cooldownFor(int lv) {
		return Math.max(1, COOLDOWN >> (lv - 1));
	}


	public ScyllaWaterBoltModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new ScyllaWaterBoltAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

	@Override
	public void modifySource(AbstractGolemEntity<?, ?> golem, CreateSourceEvent event, int value) {
		ScyllaInvulnerabilityBypass.onSpearSource(golem, event);
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc",
				CataArmorHelper.number(BURST_COUNT),
				CataArmorHelper.healthPct(CataArmorHelper.TWO_THIRDS),
				CataArmorHelper.number(BURST_COUNT_FINAL)).withStyle(ChatFormatting.GREEN));
	}

}
