package dev.xkmc.modulargolems.content.item.equipments;

import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public class MetalGolemArmorItem extends GolemEquipmentItem implements GolemModelItem {

	private final ResourceLocation model;

	public MetalGolemArmorItem(Properties properties, ArmorItem.Type type, int defense, float toughness, ResourceLocation model) {
		super(properties, type.getSlot(), GolemTypes.ENTITY_GOLEM::get, builder -> {
			ResourceLocation rl = ModularGolems.loc(type.getName() + "_armor");
			builder.put(Attributes.ARMOR, new AttributeModifier(rl, defense, AttributeModifier.Operation.ADD_VALUE));
			builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(rl.withSuffix("_toughness"), toughness, AttributeModifier.Operation.ADD_VALUE));
		});
		this.model = model;
	}

	public ResourceLocation getModelTexture() {
		ResourceLocation rl = BuiltInRegistries.ITEM.getKey(this);
		return rl.withPath(e -> "textures/equipments/" + e + ".png");
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return true;
	}

	@Override
	public int getEnchantmentValue(ItemStack stack) {
		return 15;
	}

	public ResourceLocation getModelPath() {
		return model;
	}

}