package dev.xkmc.modulargolems.compat.materials.generic;

import dev.xkmc.l2damagetracker.init.data.L2DamageTypes;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;

public class TinPlagueModifier extends GolemModifier {

    public TinPlagueModifier() {
        super(StatFilterType.MASS, 5);
    }

    @Override
    public void onHurt(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {
        var pos = entity.blockPosition();
        if (entity.level().getBiome(pos).get().coldEnoughToSnow(pos)) {
            var factor = 1 + MGConfig.COMMON.tinPlague.get() * level;
            event.setAmount(event.getAmount() * (float)factor);
        }
    }

    @Override
    public List<MutableComponent> getDetail(int level) {
        int bonus = (int) Math.round((MGConfig.COMMON.tinPlague.get() * level) * 100);
        return List.of(Component.translatable(getDescriptionId() + ".desc", bonus).withStyle(ChatFormatting.GREEN));
    }

}
