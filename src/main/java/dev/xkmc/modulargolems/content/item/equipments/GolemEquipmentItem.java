package dev.xkmc.modulargolems.content.item.equipments;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.minecraft.world.item.component.ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT;

public abstract class GolemEquipmentItem extends Item {

	private final EquipmentSlot slot;
	private final Supplier<EntityType<?>> type;
	private final Multimap<Holder<Attribute>, AttributeModifier> defaultModifiers;

	public GolemEquipmentItem(Properties properties, EquipmentSlot slot, Supplier<EntityType<?>> type,
							  Consumer<ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier>> attr) {
		super(properties);
		this.slot = slot;
		this.type = type;
		ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableMultimap.builder();
		attr.accept(builder);
		this.defaultModifiers = builder.build();

	}

	public EquipmentSlot getSlot() {
		return slot;
	}

	public boolean isFor(EntityType<?> type) {
		return this.type.get() == type;
	}

	public void forEachModifier(ItemStack stack, Entity entity, EquipmentSlot slot, BiConsumer<Holder<Attribute>, AttributeModifier> action) {
		if (this.slot != slot) return;
		if (this.type.get() != entity.getType()) return;
		defaultModifiers.forEach(action);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> list, TooltipFlag flag) {
		list.add(MGLangData.GOLEM_EQUIPMENT.get(type.get().getDescription().copy().withStyle(ChatFormatting.GOLD)));
		Multimap<Holder<Attribute>, AttributeModifier> multimap = defaultModifiers;
		if (multimap.isEmpty()) return;

		list.add(CommonComponents.EMPTY);
		list.add(Component.translatable("item.modifiers." + slot.getName()).withStyle(ChatFormatting.GRAY));

		for (var entry : multimap.entries()) {
			AttributeModifier attr = entry.getValue();
			double val = attr.amount();

			double disp;
			if (attr.operation() == AttributeModifier.Operation.ADD_VALUE) {
				disp = val;
			} else {
				disp = val * 100;
			}
			if (val > 0) {
				list.add(Component.translatable("attribute.modifier.plus." +
								attr.operation().id(), ATTRIBUTE_MODIFIER_FORMAT.format(disp),
						Component.translatable(entry.getKey().value().getDescriptionId())).withStyle(ChatFormatting.BLUE));
			} else if (val < 0) {
				disp *= -1;
				list.add(Component.translatable("attribute.modifier.take." +
								attr.operation().id(), ATTRIBUTE_MODIFIER_FORMAT.format(disp),
						Component.translatable(entry.getKey().value().getDescriptionId())).withStyle(ChatFormatting.RED));

			}
		}
	}

}
