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
import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.common.entity.BotaniaEntities;
import vazkii.botania.common.entity.ManaBurstEntity;

import java.util.List;

public class ManaBurstModifier extends ManaModifier {

    public ManaBurstModifier() {
        super(StatFilterType.ATTACK, MAX_LEVEL);
    }

    @Override
    public void onHurtTarget(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {
        var manaCost = MGConfig.COMMON.manaBurstCost.get() * level;
        var prob = MGConfig.COMMON.manaBurstProb.get() * level;
        if (entity.getRandom().nextDouble() > prob) return;
        if (BotUtils.getMana(entity) < manaCost) return;
        BotUtils.consumeMana(entity, manaCost);
        var burst = getBurst(entity);
        entity.level().addFreshEntity(burst);
    }

    public static ManaBurstEntity getBurst(LivingEntity golem) {
        ManaBurstEntity burst = new ManaBurstEntity(BotaniaEntities.MANA_BURST, golem.level());
        burst.setPos(golem.getX(), golem.getEyeY() - (double)0.1F, golem.getZ());

        burst.setBurstSourceCoords(ManaBurst.NO_SOURCE);
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
