package dev.xkmc.modulargolems.content.item.equipments;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class GolemEquipmentItem extends Item {

	private final EquipmentSlot slot;
	private final Supplier<EntityType<?>> type;

	public GolemEquipmentItem(Properties properties, EquipmentSlot slot, Supplier<EntityType<?>> type,
							  Consumer<ItemAttributeModifiers.Builder> attr) {
		super(properties.attributes(Util.make(ItemAttributeModifiers.builder(), attr).build()));
		this.slot = slot;
		this.type = type;
	}

	public EquipmentSlot getSlot() {
		return slot;
	}

	@Override
	public @Nullable EquipmentSlot getEquipmentSlot(ItemStack stack) {
		return slot;
	}

	@Override
	public boolean canEquip(ItemStack stack, EquipmentSlot armorType, LivingEntity entity) {
		return isFor(entity.getType()) && super.canEquip(stack, slot, entity);
	}

	public boolean isFor(EntityType<?> type) {
		return this.type.get() == type;
	}

	public void forEachModifier(ItemStack stack, Entity entity, EquipmentSlot slot, BiConsumer<Holder<Attribute>, AttributeModifier> action) {
		if (this.slot != slot) return;
		if (this.type.get() != entity.getType()) return;
		stack.getAttributeModifiers().forEach(slot, action);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> list, TooltipFlag flag) {
		list.add(MGLangData.GOLEM_EQUIPMENT.get(type.get().getDescription().copy().withStyle(ChatFormatting.GOLD))
				.withStyle(ChatFormatting.UNDERLINE));
	}

}
