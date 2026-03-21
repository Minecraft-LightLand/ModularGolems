package dev.xkmc.modulargolems.content.item.equipments;

import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

public class DogGolemArmorItem extends GolemEquipmentItem implements DyeableLeatherItem {

	private final ArmorMaterial mat;

	public DogGolemArmorItem(Properties properties, ArmorMaterial mat, int defense, float toughness) {
		super(properties, EquipmentSlot.CHEST, GolemTypes.ENTITY_DOG::get, builder -> {
			UUID uuid = UUID.get(EquipmentSlot.CHEST);
			builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", defense, AttributeModifier.Operation.ADDITION));
			builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", toughness, AttributeModifier.Operation.ADDITION));
		});
		this.mat = mat;
	}

	protected String namespace(String def) {
		return def;
	}

	public ResourceLocation getModelTexture(LivingEntity user, boolean override) {
		ResourceLocation rl = ForgeRegistries.ITEMS.getKey(this);
		assert rl != null;
		String name = mat.getName();
		if (override) name += "_overlay";
		return new ResourceLocation(namespace(rl.getNamespace()), "textures/entity/dog_armor/" + name + ".png");
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return true;
	}

	@Override
	public int getEnchantmentValue() {
		return mat.getEnchantmentValue();
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		if (enchantment.category == EnchantmentCategory.ARMOR ||
				enchantment.category == EnchantmentCategory.ARMOR_HEAD ||
				enchantment.category == EnchantmentCategory.ARMOR_CHEST ||
				enchantment.category == EnchantmentCategory.ARMOR_LEGS ||
				enchantment.category == EnchantmentCategory.ARMOR_FEET) {
			return true;
		}
		return super.canApplyAtEnchantingTable(stack, enchantment);
	}

}