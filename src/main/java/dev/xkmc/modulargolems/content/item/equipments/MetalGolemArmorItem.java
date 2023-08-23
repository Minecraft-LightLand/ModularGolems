package dev.xkmc.modulargolems.content.item.equipments;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.EnumMap;
import java.util.UUID;

public class MetalGolemArmorItem extends Item {

	private static final EnumMap<ArmorItem.Type, UUID> ARMOR_MODIFIER_UUID_PER_TYPE = Util.make(new EnumMap<>(ArmorItem.Type.class), (p_266744_) -> {
		p_266744_.put(ArmorItem.Type.BOOTS, UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"));
		p_266744_.put(ArmorItem.Type.LEGGINGS, UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"));
		p_266744_.put(ArmorItem.Type.CHESTPLATE, UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"));
		p_266744_.put(ArmorItem.Type.HELMET, UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150"));
	});

	private final ArmorItem.Type type;
	private final GolemEquipmentModels.GolemModelPath model;
	private final Multimap<Attribute, AttributeModifier> defaultModifiers;

	public MetalGolemArmorItem(Properties p_41383_, ArmorItem.Type type, int defense, float toughness, GolemEquipmentModels.GolemModelPath model) {
		super(p_41383_);
		this.model = model;
		this.type = type;
		UUID uuid = ARMOR_MODIFIER_UUID_PER_TYPE.get(type);
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", defense, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", toughness, AttributeModifier.Operation.ADDITION));
		this.defaultModifiers = builder.build();
	}

	@Deprecated
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot p_40390_) {
		return p_40390_ == this.type.getSlot() ? this.defaultModifiers : super.getDefaultAttributeModifiers(p_40390_);
	}

	public ResourceLocation customResLocation() {
		ResourceLocation rl = ForgeRegistries.ITEMS.getKey(this);
		assert rl != null;
		return new ResourceLocation(rl.getNamespace(), "textures/equipments/" + rl.getPath() + ".png");
	}

	public GolemEquipmentModels.GolemModelPath getModelPath() {
		return model;
	}

	public EquipmentSlot getSlot() {
		return type.getSlot();
	}

}