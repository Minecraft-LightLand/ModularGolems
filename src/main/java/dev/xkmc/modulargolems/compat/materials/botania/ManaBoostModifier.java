package dev.xkmc.modulargolems.compat.materials.botania;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import top.theillusivec4.curios.api.CuriosApi;
import vazkii.botania.api.BotaniaForgeCapabilities;

import java.util.List;

public class ManaBoostModifier extends GolemModifier {

    public ManaBoostModifier() {
        super(StatFilterType.ATTACK, MAX_LEVEL);
    }

    @Override
    public float modifyDamage(float damage, AbstractGolemEntity<?, ?> entity, int level) {
        var opt = CuriosApi.getCuriosInventory(entity).resolve();
        if (opt.isEmpty()) return damage;

        var manaItems = opt.get().findCurios(stack -> stack.getCapability(BotaniaForgeCapabilities.MANA_ITEM).orElse(null) != null);
        for (var item: manaItems) {
            var manaItem = item.stack().getCapability(BotaniaForgeCapabilities.MANA_ITEM).orElse(null);
            if (manaItem != null) {
                var remainMana = manaItem.getMana();
                var manaCost = MGConfig.COMMON.manaBoostingCost.get() * level;
                var damageBoost = MGConfig.COMMON.manaBoostingDamage.get() * level;
                if (remainMana >= manaCost) {
                    manaItem.addMana(-manaCost);
                    //System.out.printf("cost %s mana for boosting. remaining: %s\n", -manaCost, manaItem.getMana());
                    return (float) (damage * (1 + damageBoost));
                } else {
                    continue;
                }
            }
        }
        return damage;
    }

    public List<MutableComponent> getDetail(int v) {
        int bonus = (int) Math.round((MGConfig.COMMON.manaBoostingDamage.get() * v) * 100);
        int manaCost = MGConfig.COMMON.manaBoostingCost.get() * v;
        return List.of(Component.translatable(getDescriptionId() + ".desc", bonus, manaCost).withStyle(ChatFormatting.GREEN));
    }
}
