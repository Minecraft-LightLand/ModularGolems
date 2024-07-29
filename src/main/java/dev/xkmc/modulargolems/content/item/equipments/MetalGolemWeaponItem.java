package dev.xkmc.modulargolems.content.item.equipments;

import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

public class MetalGolemWeaponItem extends GolemEquipmentItem {

	public static final ResourceLocation ATK = ModularGolems.loc("weapon_attack_add");
	public static final ResourceLocation ATKP = ModularGolems.loc("weapon_attack_percent");
	public static final ResourceLocation RANGE = ModularGolems.loc("weapon_attack_range");
	public static final ResourceLocation SWEEP = ModularGolems.loc("weapon_sweep_range");

	public MetalGolemWeaponItem(Properties properties, int attackDamage, double percentAttack, float range, float sweep) {
		super(properties, EquipmentSlot.MAINHAND, GolemTypes.ENTITY_GOLEM::get, builder -> {
			if (attackDamage > 0) {
				builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATK, attackDamage, AttributeModifier.Operation.ADD_VALUE));
			}
			if (percentAttack > 0) {
				builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATKP, percentAttack, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
			}
			if (range > 0) {
				builder.put(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(RANGE, range, AttributeModifier.Operation.ADD_VALUE));
			}
			if (sweep > 0) {
				builder.put(GolemTypes.GOLEM_SWEEP.holder(), new AttributeModifier(SWEEP, sweep, AttributeModifier.Operation.ADD_VALUE));
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
