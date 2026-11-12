package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.function.BiConsumer;

public class EnderGuardianVoidRuneModifier extends GolemModifier {
	public static final int COOLDOWN = 200;
	/** 施放距离上限（格）。 */
	public static final int MAX_RANGE = 15;
	/** 施放的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;

	public static final int ROWS = 3;
	public static final int SEARCH_RANGE = 15;
	public static final float SIDE_ANGLE = 0.4F;
	public static final int TRIPLE_EXTRA = 2;

	private static final ResourceLocation VOID_RUNE = new ResourceLocation("cataclysm", "void_rune");

	public EnderGuardianVoidRuneModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new EnderGuardianVoidRuneAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

	/**
	 * 末地守卫头盔：傀儡技能召唤的虚空符文伤害无视目标无敌帧。
	 */
	@Override
	public void onAttackTarget(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		if (!CataArmorHelper.guardianHelmet(entity)) return;
		if (!isVoidRune(event.getSource())) return;
		event.getEntity().invulnerableTime = 0;
	}

	private static boolean isVoidRune(DamageSource source) {
		Entity direct = source.getDirectEntity();
		if (direct == null) return false;
		return VOID_RUNE.equals(ForgeRegistries.ENTITY_TYPES.getKey(direct.getType()));
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc",
				CataArmorHelper.healthPct(CataArmorHelper.HALF_HEALTH), CataArmorHelper.number(3)).withStyle(ChatFormatting.GREEN));
	}

}
