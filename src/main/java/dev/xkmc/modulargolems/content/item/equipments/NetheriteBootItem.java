package dev.xkmc.modulargolems.content.item.equipments;

import com.google.common.collect.ImmutableMultimap;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;

public class NetheriteBootItem extends MetalGolemArmorItem {

	public NetheriteBootItem(Properties properties, ArmorItem.Type type, int defense, float toughness, ResourceLocation model) {
		super(properties, type, defense, toughness, model);
	}

	@Override
	protected void additionalAttributes(ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder) {
		ResourceLocation rl = ModularGolems.loc(getSlot().getName() + "_armor");
		builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(rl, -0.5f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
	}

}
