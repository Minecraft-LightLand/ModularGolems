package dev.xkmc.modulargolems.compat.materials.twilightforest;

import com.google.common.collect.ImmutableMultimap;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataDispatch;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemArmorItem;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
//import twilightforest.item.NagaArmorItem;

import java.util.List;
import java.util.UUID;

public class NagaArmorItem extends MetalGolemArmorItem {

    public NagaArmorItem(Properties properties, ArmorItem.Type type, int defense, float toughness, ResourceLocation model) {
        super(properties, type, defense, toughness, model);
    }

    @Override
    protected String namespace(String def) {
        return TFDispatch.MODID;
    }



    @Override
    protected void addExtraModifiers(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder) {
        super.addExtraModifiers(builder);
        UUID uuid = UUID.get(getSlot());
        builder.put(GolemTypes.GOLEM_REGEN.get(), new AttributeModifier(uuid,
                "Naga Armor", 0.5, AttributeModifier.Operation.ADDITION));
        switch (getSlot()) {
            case CHEST -> builder.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(uuid,
                    "Naga Armor", 1, AttributeModifier.Operation.ADDITION));
            case LEGS -> builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid,
                    "Naga Armor", 0.2, AttributeModifier.Operation.MULTIPLY_BASE));
        }

    }
    @Override
    public ResourceLocation getModelTexture() {
        ResourceLocation rl = ForgeRegistries.ITEMS.getKey(this);
        assert rl != null;
        return new ResourceLocation(namespace(rl.getNamespace()), "textures/equipments/naga.png");
    }
}
