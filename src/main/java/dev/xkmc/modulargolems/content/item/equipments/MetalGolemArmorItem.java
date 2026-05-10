package dev.xkmc.modulargolems.content.item.equipments;

import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.function.Consumer;

public class MetalGolemArmorItem extends GolemEquipmentItem implements GolemModelItem {

	private final Identifier model;

	public MetalGolemArmorItem(Properties properties, ArmorItem.Type type, int defense, float toughness, Identifier model) {
		this(properties, type, defense, toughness, model, e -> {
		});
	}

	public MetalGolemArmorItem(Properties properties, ArmorItem.Type type, int defense, float toughness, Identifier model, Consumer<ItemAttributeModifiers.Builder> attr) {
		super(properties, type.getSlot(), GolemTypes.ENTITY_GOLEM::get, builder -> {
			Identifier rl = ModularGolems.loc(type.getName() + "_armor");
			builder.add(Attributes.ARMOR, new AttributeModifier(rl, defense,
					AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(type.getSlot()));
			builder.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(rl.withSuffix("_toughness"), toughness,
					AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(type.getSlot()));
			attr.accept(builder);
		});
		this.model = model;
	}

	protected String namespace(String def) {
		return def;
	}

	public Identifier getModelTexture(LivingEntity user) {
		Identifier rl = BuiltInRegistries.ITEM.getKey(this);
		return Identifier.fromNamespaceAndPath(namespace(rl.getNamespace()), "textures/equipments/" + rl.getPath() + ".png");
	}

	public Identifier getEmissiveModelTexture(LivingEntity user) {
		Identifier rl = BuiltInRegistries.ITEM.getKey(this);
		return Identifier.fromNamespaceAndPath(namespace(rl.getNamespace()), "textures/equipments/" + rl.getPath() + "_emissive.png");
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return true;
	}

	@Override
	public int getEnchantmentValue(ItemStack stack) {
		return 15;
	}

	public Identifier getModelPath() {
		return model;
	}

}