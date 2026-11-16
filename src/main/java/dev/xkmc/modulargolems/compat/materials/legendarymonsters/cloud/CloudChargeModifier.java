package dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.function.BiConsumer;
import java.util.List;

/**
 * 浮云冲锋 Modifier。挂到 cloud 材质，满血 1 段、低血量最多连冲两次，冷却同 Boss chargeCooldown(=220)。
 */
public class CloudChargeModifier extends GolemModifier implements ChargeHelper.Modifier {

	public static final int COOLDOWN = 220;
	/** 最大冲锋距离（格）。 */
	public static final double MAX_RANGE = 16;
	/** 最小冲锋距离：0 表示不限制。 */
	public static final double MIN_RANGE = 0;
	/** 低血量时最多连冲的段数。 */
	public static final int MAX_DASH = 2;
	/** 追加段冲锋的血量阈值，对应 Boss 的 {@code Crackiness.HIGH}（生命值低于上限 25%）。 */
	public static final float EXTRA_DASH_HEALTH = 0.25F;
	/** 单次冲锋最长持续 tick。 */
	public static final int MAX_TICKS = 60;

	public CloudChargeModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(1, new CloudChargeAttackGoal(entity, lv, this));
	}

	@Override
	public int getCooldown(AbstractGolemEntity<?, ?> golem, int lv) {
		return COOLDOWN;
	}

	/**
	 * 冲锋期间保留的伤害比例与其它冲锋技能共用 {@link ChargeHelper#CHARGE_DAMAGE_MULT}，
	 * 这样 tooltip 显示的减伤和实际结算不会各说各话。
	 */
	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, LivingDamageEvent event, int level) {
		if (CloudChargeAttackGoal.isCharging(entity)) {
			event.setAmount(event.getAmount() * ChargeHelper.CHARGE_DAMAGE_MULT);
		}
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		var dashes = Component.literal("" + MAX_DASH).withStyle(ChatFormatting.AQUA);
		return List.of(Component.translatable(getDescriptionId() + ".desc", ChargeHelper.chargeReduction(), dashes).withStyle(ChatFormatting.GREEN));
	}

}
