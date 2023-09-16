package dev.xkmc.modulargolems.content.item.equipments;

import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;

public class MetalGolemSpearItem extends GolemEquipmentItem {

	public MetalGolemSpearItem(Properties properties, int attackDamage, float range, float sweep) {
		super(properties, EquipmentSlot.MAINHAND, GolemTypes.ENTITY_GOLEM::get, builder -> {
			var uuid = UUID.get(EquipmentSlot.MAINHAND);
			builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(uuid, "Weapon modifier", attackDamage, AttributeModifier.Operation.ADDITION));
			builder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(uuid, "spear_range", range, AttributeModifier.Operation.ADDITION));
			builder.put(GolemTypes.GOLEM_SWEEP.get(), new AttributeModifier(uuid, "spear_sweep", sweep, AttributeModifier.Operation.ADDITION));
		});
	}

}
