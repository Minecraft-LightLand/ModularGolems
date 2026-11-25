package dev.xkmc.modulargolems.compat.materials.cataclysm.armor;

import dev.xkmc.modulargolems.compat.materials.cataclysm.CataCompatRegistry;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataDispatch;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemArmorItem;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class MonstrosityArmorItem extends MetalGolemArmorItem {

	public MonstrosityArmorItem(Properties properties, ArmorItem.Type type, int defense, float toughness, ResourceLocation model) {
		this(properties, type, defense, toughness, 0, model);
	}

	public MonstrosityArmorItem(Properties properties, ArmorItem.Type type, int defense, float toughness, float knockbackResistance, ResourceLocation model) {
		super(properties, type, defense, toughness, model, e -> {
			var id = ModularGolems.loc("monstrosity_armor_" + type.getName());
			e.add(L2DamageTracker.REDUCTION, new AttributeModifier(id, -0.2,
					AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.bySlot(type.getSlot()));
			switch (type.getSlot()) {
				case HEAD -> e.add(GolemTypes.GOLEM_REGEN, new AttributeModifier(
						id, 1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(type.getSlot()));
				case CHEST -> e.add(GolemTypes.GOLEM_SWEEP, new AttributeModifier(
						id, 1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(type.getSlot()));
				case LEGS -> e.add(Attributes.MOVEMENT_SPEED, new AttributeModifier(
						id, 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.bySlot(type.getSlot()));
			}
			MetalGolemArmorItem.addKnockback(e, type, knockbackResistance);
		});
	}
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, level, list, flag);
		// 胸甲加成所有冲锋技能，头盔与护腿仍加成地震
		switch (getSlot()) {
			case CHEST -> list.add(MGLangData.MONSTROSITY_BOOST_CHARGE
					.get(Math.round(MGConfig.COMMON.monstrosityChargeBonus.get() * 100) + "%")
					.withStyle(ChatFormatting.GOLD));
			case HEAD, LEGS -> list.add(CataArmorHelper.damageLine(MGConfig.COMMON.earthquakeArmorBonus.get(),
					CataArmorHelper.skillName(CataCompatRegistry.EARTHQUAKE.get()))
					.withStyle(ChatFormatting.GOLD));
		}
	}

	@Override
	public boolean emissive(LivingEntity user, ItemStack stack) {
		return true;
	}

	@Override
	protected String namespace(String def) {
		return CataDispatch.MODID;
	}

}
