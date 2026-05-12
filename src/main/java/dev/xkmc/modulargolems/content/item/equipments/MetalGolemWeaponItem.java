package dev.xkmc.modulargolems.content.item.equipments;

import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.Weapon;

import java.util.function.Consumer;

public class MetalGolemWeaponItem extends GolemEquipmentItem {

	public static final Identifier ATK = ModularGolems.loc("weapon_attack_add");
	public static final Identifier ATKP = ModularGolems.loc("weapon_attack_percent");
	public static final Identifier RANGE = ModularGolems.loc("weapon_attack_range");
	public static final Identifier SWEEP = ModularGolems.loc("weapon_sweep_range");

	public MetalGolemWeaponItem(Properties properties, int attackDamage, double percentAttack, float range, float sweep, int shieldBreak) {
		this(properties.component(DataComponents.WEAPON, new Weapon(0, shieldBreak)), attackDamage, percentAttack, range, sweep, e -> {
		});
	}

	public MetalGolemWeaponItem(Properties properties, int attackDamage, double percentAttack, float range, float sweep, Consumer<ItemAttributeModifiers.Builder> attr) {
		super(properties.enchantable(15),
				EquipmentSlot.MAINHAND, GolemTypes.ENTITY_GOLEM::get, builder -> {
					if (attackDamage > 0) {
						builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATK, attackDamage,
								AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
					}
					if (percentAttack > 0) {
						builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATKP, percentAttack,
								AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.MAINHAND);
					}
					if (range > 0) {
						builder.add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(RANGE, range,
								AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
					}
					if (sweep > 0) {
						builder.add(GolemTypes.GOLEM_SWEEP.holder(), new AttributeModifier(SWEEP, sweep,
								AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
					}
					attr.accept(builder);
				});
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext level, TooltipDisplay disp, Consumer<Component> list, TooltipFlag flag) {
		if (stack.is(MGTagGen.SHIELD_BREAKER_WEAPONS))
			list.accept(MGLangData.SHIELD_BREAK.get());
		super.appendHoverText(stack, level, disp, list, flag);
	}

}
