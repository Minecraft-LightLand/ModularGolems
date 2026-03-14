package dev.xkmc.modulargolems.content.item.equipments;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IGolemEquipmentItem {

	boolean isFor(EntityType<?> type);

	EquipmentSlot getSlot();

	Multimap<Attribute, AttributeModifier> getGolemModifiers(ItemStack stack, @Nullable Entity user, EquipmentSlot slot);

}
