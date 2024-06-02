package dev.xkmc.modulargolems.compat.materials.botania;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import top.theillusivec4.curios.api.CuriosApi;
import vazkii.botania.api.BotaniaForgeCapabilities;

import java.util.List;

public class ManaProductionModifier extends ManaModifier {

    public ManaProductionModifier() {
        super(StatFilterType.HEALTH, MAX_LEVEL);
    }

    @Override
    public double onHealTick(double heal, AbstractGolemEntity<?, ?> entity, int level) {
        int prod = MGConfig.COMMON.manaProductionVal.get() * level;
        BotUtils.generateMana(entity, prod);
        return heal;
    }

    public List<MutableComponent> getDetail(int v) {
        var prod = MGConfig.COMMON.manaProductionVal.get() * v;
        return List.of(Component.translatable(getDescriptionId() + ".desc", prod).withStyle(ChatFormatting.GREEN));
    }
}
