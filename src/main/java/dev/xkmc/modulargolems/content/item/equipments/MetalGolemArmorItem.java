package dev.xkmc.modulargolems.content.item.equipments;

import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

public class MetalGolemArmorItem extends GolemEquipmentItem implements GolemModelItem {

	private final ResourceLocation model;

	public MetalGolemArmorItem(Properties properties, EquipmentSlot type, int defense, float toughness, ResourceLocation model) {
		super(properties, type, GolemTypes.ENTITY_GOLEM::get, builder -> {
			UUID uuid = UUID.get(type);
			builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", defense, AttributeModifier.Operation.ADDITION));
			builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", toughness, AttributeModifier.Operation.ADDITION));
		});
		this.model = model;
	}

	public ResourceLocation getModelTexture() {
		ResourceLocation rl = ForgeRegistries.ITEMS.getKey(this);
		assert rl != null;
		return new ResourceLocation(rl.getNamespace(), "textures/equipments/" + rl.getPath() + ".png");
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return true;
	}

	@Override
	public int getEnchantmentValue() {
		return 15;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		if (enchantment.category == EnchantmentCategory.ARMOR) {
			return true;
		}
		if (enchantment.category == EnchantmentCategory.ARMOR_HEAD && getSlot() == EquipmentSlot.HEAD) {
			return true;
		}
		if (enchantment.category == EnchantmentCategory.ARMOR_CHEST && getSlot() == EquipmentSlot.CHEST) {
			return true;
		}
		if (enchantment.category == EnchantmentCategory.ARMOR_LEGS && getSlot() == EquipmentSlot.LEGS) {
			return true;
		}
		if (enchantment.category == EnchantmentCategory.ARMOR_FEET && getSlot() == EquipmentSlot.FEET) {
			return true;
		}
		return super.canApplyAtEnchantingTable(stack, enchantment);
	}

	public ResourceLocation getModelPath() {
		return model;
	}

}