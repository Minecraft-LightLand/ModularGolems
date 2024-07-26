package dev.xkmc.modulargolems.content.item.equipments;

import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;

public class MetalGolemWeaponItem extends GolemEquipmentItem {

	public MetalGolemWeaponItem(Properties properties, int attackDamage, double percentAttack, float range, float sweep) {
		super(properties, EquipmentSlot.MAINHAND, GolemTypes.ENTITY_GOLEM::get, builder -> {
			var uuid = UUID.get(EquipmentSlot.MAINHAND);
			if (attackDamage > 0) {
				builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(uuid, "Weapon modifier", attackDamage, AttributeModifier.Operation.ADD_VALUE));
			}
			if (percentAttack > 0) {
				builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(uuid, "Weapon modifier", percentAttack, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
			}
			if (range > 0) {
				builder.put(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(uuid, "spear_range", range, AttributeModifier.Operation.ADD_VALUE));
			}
			if (sweep > 0) {
				builder.put(GolemTypes.GOLEM_SWEEP.get(), new AttributeModifier(uuid, "spear_sweep", sweep, AttributeModifier.Operation.ADD_VALUE));
			}
		});
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return true;
	}

	@Override
	public int getEnchantmentValue() {
		return 15;
	}

}
