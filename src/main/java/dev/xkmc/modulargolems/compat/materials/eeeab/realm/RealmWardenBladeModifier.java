package dev.xkmc.modulargolems.compat.materials.eeeab.realm;

import com.eeeab.eeeabsmobs.sever.entity.util.ModEntityUtils;
import com.eeeab.eeeabsmobs.sever.init.EffectInit;
import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.l2damagetracker.init.data.L2DamageTypes;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.List;

/**
 * 界域之刃：生命值低于上限一半时提升攻击伤害，近战命中的目标叠加感电效果（跟随界域戍卫：一般 5 层、困难 10 层）。
 * 2 级时攻击伤害提升加倍。感电叠加逻辑与 {@code EntityRealmWarden.doHurtTarget} 一致。
 */
public class RealmWardenBladeModifier extends GolemModifier {

	public static final float HEALTH_THRESHOLD = 0.5F;
	public static final float ATTACK_BONUS_PER_LEVEL = 0.1F;
	public static final int STACK_NORMAL = 5, STACK_HARD = 10;
	public static final int EFFECT_DURATION = 200;
	public static final float ELECTRIFIED_PER_LAYER = 0.05F;
	public static final float ELECTRIFIED_CAP = 0.5F;

	public RealmWardenBladeModifier() {
		super(StatFilterType.ATTACK, 2);
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		var thr = Component.literal(Math.round(HEALTH_THRESHOLD * 100) + "%").withStyle(ChatFormatting.AQUA);
		var atk = Component.literal(Math.round(ATTACK_BONUS_PER_LEVEL * v * 100) + "%").withStyle(ChatFormatting.AQUA);
		return List.of(Component.translatable(getDescriptionId() + ".desc", thr, atk).withStyle(ChatFormatting.GREEN));
	}

	@Override
	public void modifyDamage(AttackCache cache, AbstractGolemEntity<?, ?> entity, int level) {
		float bonus = 0;
		if (entity.getHealth() < entity.getMaxHealth() * HEALTH_THRESHOLD) {
			bonus += ATTACK_BONUS_PER_LEVEL * level;
		}
		LivingEntity target = cache.getAttackTarget();
		if (target != null) {
			MobEffectInstance ins = target.getEffect(EffectInit.ELECTRIFIED_EFFECT.get());
			if (ins != null) {
				// 与 EntityRealmWarden.doHurtTarget 一致：每层感电 +5%，封顶 +50%
				bonus += Mth.clamp((ins.getAmplifier() + 1) * ELECTRIFIED_PER_LAYER, 0F, ELECTRIFIED_CAP);
			}
		}
		if (bonus > 0) {
			cache.addHurtModifier(DamageModifier.multTotal(1 + bonus));
		}
	}

	@Override
	public void onAttackTarget(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		if (entity.level().isClientSide()) return;
		if (!event.getSource().is(L2DamageTypes.DIRECT)) return;
		// 叠感电仅二阶段（生命 < 50%）生效，与 EntityRealmWarden 一致
		if (entity.getHealth() >= entity.getMaxHealth() * HEALTH_THRESHOLD) return;
		LivingEntity target = event.getEntity();
		boolean hard = entity.level().getDifficulty() == Difficulty.HARD;
		// 与 EntityRealmWarden.doHurtTarget 一致：叠感电，一般上限 5 层 / 困难 10 层
		ModEntityUtils.addEffectStackingAmplifier(null, target, EffectInit.ELECTRIFIED_EFFECT.get(),
				EFFECT_DURATION, hard ? STACK_HARD : STACK_NORMAL, false, true, true, false);
	}

}
