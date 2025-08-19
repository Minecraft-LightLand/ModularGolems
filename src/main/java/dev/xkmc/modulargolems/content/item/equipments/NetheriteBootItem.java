package dev.xkmc.modulargolems.content.item.equipments;

import com.google.common.collect.ImmutableMultimap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;

import java.util.UUID;

public class NetheriteBootItem extends MetalGolemArmorItem {

	public NetheriteBootItem(Properties properties, ArmorItem.Type type, int defense, float toughness, ResourceLocation model) {
		super(properties, type, defense, toughness, model);
	}

	@Override
	protected void addExtraModifiers(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder) {
		super.addExtraModifiers(builder);
		UUID uuid = UUID.get(getSlot());
		builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid, "Armor modifier", -0.5f, AttributeModifier.Operation.MULTIPLY_TOTAL));
	}

}
