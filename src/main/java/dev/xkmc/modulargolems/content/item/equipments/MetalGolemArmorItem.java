package dev.xkmc.modulargolems.content.item.equipments;

import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

public class MetalGolemArmorItem extends GolemEquipmentItem implements GolemModelItem {

	private final GolemEquipmentModels.GolemModelPath model;

	public MetalGolemArmorItem(Properties properties, ArmorItem.Type type, int defense, float toughness, GolemEquipmentModels.GolemModelPath model) {
		super(properties, type.getSlot(), GolemTypes.ENTITY_GOLEM::get, builder -> {
			UUID uuid = UUID.get(type.getSlot());
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

	public GolemEquipmentModels.GolemModelPath getModelPath() {
		return model;
	}

}