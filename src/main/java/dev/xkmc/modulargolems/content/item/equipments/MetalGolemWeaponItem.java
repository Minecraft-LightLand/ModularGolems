package dev.xkmc.modulargolems.content.item.equipments;

import dev.xkmc.l2library.util.math.MathHelper;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MetalGolemWeaponItem extends GolemEquipmentItem {

	public MetalGolemWeaponItem(Properties properties, int attackDamage, double percentAttack, float range, float sweep) {
		super(properties, EquipmentSlot.MAINHAND, GolemTypes.ENTITY_GOLEM::get, builder -> {
			var uuid = UUID.get(EquipmentSlot.MAINHAND);
			var perc = MathHelper.getUUIDFromString("golem_percent_attack");
			if (attackDamage > 0) {
				builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(uuid, "Weapon modifier", attackDamage, AttributeModifier.Operation.ADDITION));
			}
			if (percentAttack > 0) {
				builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(perc, "Weapon modifier", percentAttack, AttributeModifier.Operation.MULTIPLY_BASE));
			}
			if (range > 0) {
				builder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(uuid, "weapon_range", range, AttributeModifier.Operation.ADDITION));
			}
			if (sweep > 0) {
				builder.put(GolemTypes.GOLEM_SWEEP.get(), new AttributeModifier(uuid, "weapon_sweep", sweep, AttributeModifier.Operation.ADDITION));
			}
		});
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		if (stack.is(MGTagGen.SHIELD_BREAKER_WEAPONS))
			list.add(MGLangData.SHIELD_BREAK.get());
		super.appendHoverText(stack, level, list, flag);
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
	public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
		return stack.is(MGTagGen.SHIELD_BREAKER_WEAPONS);
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction action) {
		return action == ToolActions.SWORD_DIG || super.canPerformAction(stack, action);
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		if (enchantment.category == EnchantmentCategory.WEAPON) {
			return true;
		}
		return super.canApplyAtEnchantingTable(stack, enchantment);
	}

}
