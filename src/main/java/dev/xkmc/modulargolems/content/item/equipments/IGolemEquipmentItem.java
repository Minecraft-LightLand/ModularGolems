package dev.xkmc.modulargolems.content.item.equipments;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;

public interface IGolemEquipmentItem {

	boolean isFor(EntityType<?> type);

	EquipmentSlot getSlot();

	void forEachModifier(ItemStack stack, Entity user, EquipmentSlot slot, BiConsumer<Holder<Attribute>, AttributeModifier> action);

}
