package dev.xkmc.modulargolems.content.item.equipments;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.xkmc.l2library.util.math.MathHelper;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class GolemEquipmentItem extends Item implements IGolemEquipmentItem {

	protected static final EnumMap<EquipmentSlot, UUID> UUID;

	static {
		UUID = new EnumMap<>(EquipmentSlot.class);
		for (var e : EquipmentSlot.values()) {
			UUID.put(e, MathHelper.getUUIDFromString(ModularGolems.MODID + ":metalgolem_" + e.getName()));
		}
	}

	private final EquipmentSlot slot;
	private final Supplier<EntityType<?>> type;
	private final Multimap<Attribute, AttributeModifier> defaultModifiers;

	public GolemEquipmentItem(Properties properties, EquipmentSlot slot, Supplier<EntityType<?>> type,
							  Consumer<ImmutableMultimap.Builder<Attribute, AttributeModifier>> attr) {
		super(properties);
		this.slot = slot;
		this.type = type;
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		attr.accept(builder);
		addExtraModifiers(builder);
		this.defaultModifiers = builder.build();
	}

	protected void addExtraModifiers(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder) {
	}

	public EquipmentSlot getSlot() {
		return slot;
	}

	@Override
	public @Nullable EquipmentSlot getEquipmentSlot(ItemStack stack) {
		return slot;
	}

	@Override
	public boolean canEquip(ItemStack stack, EquipmentSlot slot, Entity entity) {
		return isFor(entity.getType()) && super.canEquip(stack, slot, entity);
	}

	public boolean isFor(EntityType<?> type) {
		return this.type.get() == type;
	}

	public Multimap<Attribute, AttributeModifier> getGolemModifiers(ItemStack stack, @Nullable Entity entity, EquipmentSlot slot) {
		if (entity != null && this.type.get() != entity.getType())
			return ImmutableMultimap.of();
		return stack.getAttributeModifiers(slot);
	}

	@Deprecated
	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
		return slot == this.slot ? defaultModifiers : super.getDefaultAttributeModifiers(slot);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		list.add(MGLangData.GOLEM_EQUIPMENT.get(type.get().getDescription().copy().withStyle(ChatFormatting.GOLD))
				.withStyle(ChatFormatting.UNDERLINE));
	}

	@Override
	public boolean canBeHurtBy(DamageSource source) {
		return !getDefaultInstance().is(MGTagGen.TOUGH_ITEM) || !(
				source.is(DamageTypeTags.IS_FIRE) ||
						source.is(DamageTypeTags.IS_EXPLOSION) ||
						source.is(DamageTypeTags.IS_LIGHTNING));
	}

}
