package dev.xkmc.modulargolems.compat.materials.twilightforest.equipments;

import com.google.common.collect.ImmutableMultimap;
import dev.xkmc.modulargolems.compat.materials.twilightforest.TFDispatch;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemArmorItem;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;

import java.util.UUID;

public class NagaArmorItem extends MetalGolemArmorItem {

	public NagaArmorItem(Properties properties, ArmorItem.Type type, int defense, float toughness, ResourceLocation model) {
		super(properties, type, defense, toughness, model);
	}

	@Override
	protected String namespace(String def) {
		return TFDispatch.MODID;
	}

	@Override
	protected void addExtraModifiers(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder) {
		super.addExtraModifiers(builder);
		UUID uuid = UUID.get(getSlot());
		builder.put(GolemTypes.GOLEM_REGEN.get(), new AttributeModifier(uuid,
				"Naga Armor", 0.5, AttributeModifier.Operation.ADDITION));
		switch (getSlot()) {
			case CHEST -> builder.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(uuid,
					"Naga Armor", 1, AttributeModifier.Operation.ADDITION));
			case LEGS -> builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid,
					"Naga Armor", 0.2, AttributeModifier.Operation.MULTIPLY_BASE));
		}

	}

	@Override
	public ResourceLocation getModelTexture(LivingEntity user) {
		return new ResourceLocation(TFDispatch.MODID, "textures/equipments/naga.png");
	}

}
