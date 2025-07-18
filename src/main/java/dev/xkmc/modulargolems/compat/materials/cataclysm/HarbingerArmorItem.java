package dev.xkmc.modulargolems.compat.materials.cataclysm;

import com.google.common.collect.ImmutableMultimap;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemArmorItem;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;

import java.util.UUID;

public class HarbingerArmorItem extends MetalGolemArmorItem {

	public HarbingerArmorItem(Properties properties, ArmorItem.Type type, int defense, float toughness, ResourceLocation model) {
		super(properties, type, defense, toughness, model);
	}

	@Override
	public boolean emissive() {
		return true;
	}

	@Override
	protected String namespace(String def) {
		return CataDispatch.MODID;
	}

	@Override
	protected void addExtraModifiers(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder) {
		super.addExtraModifiers(builder);
		UUID uuid = UUID.get(getSlot());
		builder.put(L2DamageTracker.ABSORB.get(), new AttributeModifier(uuid, "Harbinger Armor", 1, AttributeModifier.Operation.ADDITION));
		switch (getSlot()) {
			case HEAD -> builder.put(GolemTypes.GOLEM_REGEN.get(), new AttributeModifier(uuid,
					"Harbinger Armor", 1, AttributeModifier.Operation.ADDITION));
			case CHEST -> builder.put(GolemTypes.GOLEM_SWEEP.get(), new AttributeModifier(uuid,
					"Harbinger Armor", 1, AttributeModifier.Operation.ADDITION));
			case LEGS -> builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid,
					"Harbinger Armor", 1, AttributeModifier.Operation.MULTIPLY_BASE));
		}
	}

}
