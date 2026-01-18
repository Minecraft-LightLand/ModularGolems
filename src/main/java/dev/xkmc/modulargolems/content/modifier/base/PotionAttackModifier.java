package dev.xkmc.modulargolems.content.modifier.base;

import dev.xkmc.l2library.base.effects.EffectUtil;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;
import java.util.function.Function;

public class PotionAttackModifier extends GolemModifier {

	// 这个函数用于根据给定的等级生成相应的药水效果实例
	private final Function<Integer, MobEffectInstance> func;

	// 构造函数
	public PotionAttackModifier(StatFilterType type, int maxLevel, Function<Integer, MobEffectInstance> func) {
		super(type, maxLevel);
		this.func = func;
	}

	@Override
	public void onHurtTarget(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {
		applyPotion(entity, event.getEntity(), level);
	}

	@Override
	public void onHurt(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {
		if (event.getSource().getDirectEntity() instanceof LivingEntity attacker) {
			// 向伤害来源施加药水效果
			applyPotion(entity, attacker, level);
		}
	}

	// 实际添加药水效果到目标实体上,是上两个方法的效果实现
	private void applyPotion(AbstractGolemEntity<?, ?> self, LivingEntity target, int level) {
		// 避免客户端执行不必要的逻辑
		if (!target.level().isClientSide()) {
			EffectUtil.addEffect(target, func.apply(level), EffectUtil.AddReason.NONE, self);
		}
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		MobEffectInstance ins = func.apply(v);
		MutableComponent lang = Component.translatable(ins.getDescriptionId());
		MobEffect mobeffect = ins.getEffect();
		if (ins.getAmplifier() > 0) {
			lang = Component.translatable("potion.withAmplifier", lang,
					Component.translatable("potion.potency." + ins.getAmplifier()));
		}
		// 如果药水效果的持续时间（duration）大于等于 20
		if (ins.getDuration() >= 20) {
			lang = Component.translatable("potion.withDuration", lang,
					MobEffectUtil.formatDuration(ins, 1));
		}
		lang = lang.withStyle(mobeffect.getCategory().getTooltipFormatting());
		return List.of(MGLangData.POTION_ATTACK.get(lang).withStyle(ChatFormatting.GREEN));
	}

}
