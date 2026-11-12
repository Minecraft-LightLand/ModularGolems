package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.function.BiConsumer;

public class HarbingerHomingMissileModifier extends GolemModifier {
	public static final int COOLDOWN = 100;

	public static final int SHOT_COUNT = 3;
	public static final int MAX_RANGE = 35;
	/** 施放的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;

	public HarbingerHomingMissileModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new HarbingerHomingMissileAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

	/**
	 * 先行者胸甲加成的发射物：凋灵巡导弹、凋灵飞弹、凋灵榴弹。
	 */
	private static boolean isBoosted(DamageSource source) {
		return GolemCataProxy.isMissile(source) || GolemCataProxy.isWitherMissile(source) || GolemCataProxy.isHowitzer(source);
	}

	/**
	 * 佩戴先行者胸甲时，导弹/飞弹/榴弹无视目标的受击无敌帧（不再加伤害）。
	 */
	@Override
	public void onAttackTarget(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		if (!isBoosted(event.getSource())) return;
		if (!CataArmorHelper.harbingerChest(entity)) return;
		event.getEntity().invulnerableTime = 0;
	}

	@Override
	public java.util.List<MutableComponent> getDetail(int v) {
		return java.util.List.of(Component.translatable(getDescriptionId() + ".desc",
				CataArmorHelper.healthPct(CataArmorHelper.HALF_HEALTH)).withStyle(ChatFormatting.GREEN));
	}

}
