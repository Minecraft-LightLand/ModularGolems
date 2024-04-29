package dev.xkmc.modulargolems.compat.materials.botania;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import top.theillusivec4.curios.api.CuriosApi;
import vazkii.botania.api.BotaniaForgeCapabilities;

import java.util.List;

public class ManaProductionModifier extends GolemModifier {

    public ManaProductionModifier() {
        super(StatFilterType.HEALTH, MAX_LEVEL);
    }

    @Override
    public double onInventoryHealTick(double heal, GolemModifier.HealingContext ctx, int level) {
        if (ctx.owner() instanceof LivingEntity le) {
            var prod = MGConfig.COMMON.manaProductionVal.get() * level;

            var opt = CuriosApi.getCuriosInventory(le).resolve();
            if (opt.isEmpty()) return heal;

            var manaItems = opt.get().findCurios(stack -> stack.getCapability(BotaniaForgeCapabilities.MANA_ITEM).orElse(null) != null);
            for (var item: manaItems) {
                var manaItem = item.stack().getCapability(BotaniaForgeCapabilities.MANA_ITEM).orElse(null);
                if (manaItem != null) {
                    manaItem.addMana(prod);
                    //System.out.println(manaItem.getMana());
                    return heal;
                }
            }
        }
        return heal;
    }

    public List<MutableComponent> getDetail(int v) {
        var prod = MGConfig.COMMON.manaProductionVal.get() * v;
        return List.of(Component.translatable(getDescriptionId() + ".desc", prod).withStyle(ChatFormatting.GREEN));
    }
}
