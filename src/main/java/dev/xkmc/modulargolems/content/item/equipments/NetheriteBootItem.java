package dev.xkmc.modulargolems.content.item.equipments;

import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.equipment.ArmorType;

public class NetheriteBootItem extends MetalGolemArmorItem {

	public NetheriteBootItem(Properties properties, ArmorType type, int defense, float toughness, Identifier model) {
		super(properties, type, defense, toughness, model, e -> e.add(Attributes.MOVEMENT_SPEED,
				new AttributeModifier(ModularGolems.loc(EquipmentSlot.FEET.getName() + "_armor"), -0.5f,
						AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.FEET));
	}

}
