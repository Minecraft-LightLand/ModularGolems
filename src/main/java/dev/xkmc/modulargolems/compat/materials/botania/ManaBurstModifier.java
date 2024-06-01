package dev.xkmc.modulargolems.compat.materials.botania;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import top.theillusivec4.curios.api.CuriosApi;
import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.common.entity.BotaniaEntities;
import vazkii.botania.common.entity.ManaBurstEntity;

import java.util.List;

public class ManaBurstModifier extends GolemModifier {

    public ManaBurstModifier() {
        super(StatFilterType.ATTACK, MAX_LEVEL);
    }

    @Override
    public void onHurtTarget(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {
        var opt = CuriosApi.getCuriosInventory(entity).resolve();
        if (opt.isEmpty()) return;

        var manaItems = opt.get().findCurios(stack -> stack.getCapability(BotaniaForgeCapabilities.MANA_ITEM).orElse(null) != null);
        for (var item: manaItems) {
            var manaItem = item.stack().getCapability(BotaniaForgeCapabilities.MANA_ITEM).orElse(null);
            if (manaItem != null) {
                var remainMana = manaItem.getMana();
                var manaCost = MGConfig.COMMON.manaBurstCost.get() * level;
                var prob = MGConfig.COMMON.manaBurstProb.get() * level;
                if (remainMana >= manaCost) {
                    if (entity.getRandom().nextDouble() < prob) {
                        manaItem.addMana(-manaCost);
                        var burst = getBurst(entity);
                        entity.level().addFreshEntity(burst);
                        return;
                    }
                } else {
                    continue;
                }
            }
        }
        return;
    }

    public static ManaBurstEntity getBurst(LivingEntity golem) {
        ManaBurstEntity burst = new ManaBurstEntity(BotaniaEntities.MANA_BURST, golem.level());
        burst.setPos(golem.getX(), golem.getEyeY() - (double)0.1F, golem.getZ());

        burst.setBurstSourceCoords(ManaBurst.NO_SOURCE);
        //burst.setRot(golem.getYRot() + 180, -golem.getXRot());
        burst.setYRot((golem.getYRot() + 180) % 360.0F);
        burst.setXRot(-golem.getXRot() % 360.0F);
        burst.setDeltaMovement(ManaBurstEntity.calculateBurstVelocity(burst.getXRot(), burst.getYRot()));

        float motionModifier = 7F;

        burst.setColor(0x20FF20);
        burst.setMana(100);
        burst.setStartingMana(100);
        burst.setMinManaLoss(40);
        burst.setManaLossPerTick(4F);
        burst.setGravity(0F);
        burst.setDeltaMovement(burst.getDeltaMovement().scale(motionModifier));

        return burst;
    }

    public List<MutableComponent> getDetail(int v) {
        int prob = (int) Math.round((MGConfig.COMMON.manaBurstProb.get() * v) * 100);
        int manaCost = MGConfig.COMMON.manaBurstCost.get();
        return List.of(Component.translatable(getDescriptionId() + ".desc", prob, manaCost).withStyle(ChatFormatting.GREEN));
    }
}
