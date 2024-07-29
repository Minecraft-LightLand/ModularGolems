package dev.xkmc.modulargolems.content.modifier.base;

import dev.xkmc.l2core.base.effects.EffectUtil;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.function.Function;

public class PotionAttackModifier extends GolemModifier {

	private final Function<Integer, MobEffectInstance> func;

	public PotionAttackModifier(StatFilterType type, int maxLevel, Function<Integer, MobEffectInstance> func) {
		super(type, maxLevel);
		this.func = func;
	}

	@Override
	public void postHurtTarget(AbstractGolemEntity<?, ?> entity, DamageData.DefenceMax event, int level) {
		applyPotion(entity, event.getTarget(), level);
	}

	@Override
	public void postDamaged(AbstractGolemEntity<?, ?> entity, DamageData.DefenceMax event, int level) {
		if (event.getSource().getDirectEntity() instanceof LivingEntity attacker) {
			applyPotion(entity, attacker, level);
		}
	}

	private void applyPotion(AbstractGolemEntity<?, ?> self, LivingEntity target, int level) {
		if (!target.level().isClientSide()) {
			EffectUtil.addEffect(target, func.apply(level), self);
		}
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		MobEffectInstance ins = func.apply(v);
		MutableComponent lang = Component.translatable(ins.getDescriptionId());
		var mobeffect = ins.getEffect();
		if (ins.getAmplifier() > 0) {
			lang = Component.translatable("potion.withAmplifier", lang,
					Component.translatable("potion.potency." + ins.getAmplifier()));
		}
		if (ins.getDuration() >= 20) {
			lang = Component.translatable("potion.withDuration", lang,
					MobEffectUtil.formatDuration(ins, 1, 20));
		}
		lang = lang.withStyle(mobeffect.value().getCategory().getTooltipFormatting());
		return List.of(MGLangData.POTION_ATTACK.get(lang).withStyle(ChatFormatting.GREEN));
	}

}
