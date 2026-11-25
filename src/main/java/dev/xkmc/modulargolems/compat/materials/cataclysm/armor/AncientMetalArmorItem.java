package dev.xkmc.modulargolems.compat.materials.cataclysm.armor;

import dev.xkmc.modulargolems.compat.materials.cataclysm.CataDispatch;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers.AncientRemnantSandstormGroup;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemArmorItem;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class AncientMetalArmorItem extends MetalGolemArmorItem {

	public AncientMetalArmorItem(Properties properties, ArmorItem.Type type, int defense, float toughness,
			float knockbackResistance, ResourceLocation model) {
		super(properties, type, defense, toughness, model, e -> {
			var id = ModularGolems.loc("ancient_metal_armor_" + type.getName());
			e.add(L2DamageTracker.REDUCTION, new AttributeModifier(id, -0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
					EquipmentSlotGroup.bySlot(type.getSlot()));
			switch (type.getSlot()) {
				case HEAD -> e.add(GolemTypes.GOLEM_REGEN, new AttributeModifier(id, 1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(type.getSlot()));
				case CHEST -> e.add(GolemTypes.GOLEM_SWEEP, new AttributeModifier(id, 1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(type.getSlot()));
				case LEGS -> e.add(Attributes.MOVEMENT_SPEED, new AttributeModifier(id, 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.bySlot(type.getSlot()));
			}
			MetalGolemArmorItem.addKnockback(e, type, knockbackResistance);
		});
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, level, list, flag);
		switch (getSlot()) {
			case HEAD -> list.add(MGLangData.ANCIENT_SANDSTORM_BOOST
					.get(AncientRemnantSandstormGroup.helmetSpeedPercent() + "%",
							CataArmorHelper.skillName(CataCompatRegistry.SANDSTORM.get()),
							CataArmorHelper.skillName(CataCompatRegistry.SANDSTORM.get()),
							CataArmorHelper.skillName(CataCompatRegistry.ANCIENT_RENANT_STELE.get()))
					.withStyle(ChatFormatting.GOLD));
			case CHEST -> list.add(MGLangData.SKILL_IGNORE_HEALTH.get(CataArmorHelper.materialName("ancient_metal"))
					.withStyle(ChatFormatting.GOLD));
			case LEGS -> list.add(MGLangData.ANCIENT_SHOCKWAVE_BOOST.get().withStyle(ChatFormatting.GOLD));
		}
	}


	@Override
	protected String namespace(String def) {
		return CataDispatch.MODID;
	}

}
