package dev.xkmc.modulargolems.compat.materials.generic;

import dev.xkmc.l2library.base.effects.EffectUtil;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;

public class WeidingModifier extends GolemModifier {

    public WeidingModifier() {
        super(StatFilterType.MASS, MAX_LEVEL);
    }

    @Override
    public void onHurtTarget(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {
        if (entity.isOnFire()) {
            var effect = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, level - 1);
            EffectUtil.addEffect(event.getEntity(), effect, EffectUtil.AddReason.NONE, entity);
        }

    }

    public List<MutableComponent> getDetail(int v) {
        MobEffectInstance ins = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, v - 1);
        MutableComponent lang = Component.translatable(ins.getDescriptionId());
        MobEffect mobeffect = ins.getEffect();
        if (ins.getAmplifier() > 0) {
            lang = Component.translatable("potion.withAmplifier", lang,
                    Component.translatable("potion.potency." + ins.getAmplifier()));
        }
        if (ins.getDuration() >= 20) {
            lang = Component.translatable("potion.withDuration", lang,
                    MobEffectUtil.formatDuration(ins, 1));
        }
        lang = lang.withStyle(mobeffect.getCategory().getTooltipFormatting());
        return List.of(Component.translatable(getDescriptionId() + ".desc", lang).withStyle(ChatFormatting.GREEN));
    }
}
