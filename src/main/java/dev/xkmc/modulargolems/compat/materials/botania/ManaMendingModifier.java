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

public class ManaMendingModifier extends GolemModifier {

    public ManaMendingModifier() {
        super(StatFilterType.HEALTH, MAX_LEVEL);
    }

    @Override
    public double onInventoryHealTick(double heal, GolemModifier.HealingContext ctx, int level) {
        if (ctx.owner() instanceof LivingEntity le) {
            var maxHeal = le.getMaxHealth() - le.getHealth();
            if (maxHeal <= 0) return heal;
            var opt = CuriosApi.getCuriosInventory(le).resolve();
            if (opt.isEmpty()) return heal;
            var manaItems = opt.get().findCurios(stack -> stack.getCapability(BotaniaForgeCapabilities.MANA_ITEM).orElse(null) != null);
            for (var item: manaItems) {
                var manaItem = item.stack().getCapability(BotaniaForgeCapabilities.MANA_ITEM).orElse(null);
                if (manaItem != null) {
                    var remainMana = manaItem.getMana();
                    var eff = MGConfig.COMMON.manaMendingCost.get();
                    var val = MGConfig.COMMON.manaMendingVal.get() * level;
                    if (remainMana < eff) continue;

                    var manaHeal = Math.min(1F * remainMana / eff, val);
                    manaHeal = Math.min(manaHeal, maxHeal);
                    int manaCost = (int) (-manaHeal * eff);
                    manaItem.addMana(manaCost);
                    //System.out.printf("cost %s mana for mending. remaining: %s\n", manaCost, manaItem.getMana());
                    return heal + manaHeal;
                }
            }
        }
        return heal;
    }

    public List<MutableComponent> getDetail(int v) {
        int eff = MGConfig.COMMON.manaMendingCost.get();
        double val = MGConfig.COMMON.manaMendingVal.get() * v;
        return List.of(Component.translatable(getDescriptionId() + ".desc", val, eff).withStyle(ChatFormatting.GREEN));
    }
}
