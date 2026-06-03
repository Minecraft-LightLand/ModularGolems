package dev.xkmc.modulargolems.compat.materials.cataclysm.armor;

import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataDispatch;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemArmorItem;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class MaledictusArmorItem extends MetalGolemArmorItem {

	public MaledictusArmorItem(Properties properties, ArmorItem.Type type, int defense, float toughness, ResourceLocation model) {
		super(properties, type, defense, toughness, model, e -> {
			var id = ModularGolems.loc("maledictus_armor_" + type.getName());
			e.add(L2DamageTracker.ABSORB, new AttributeModifier(id, 1,
					AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(type.getSlot()));
			switch (type.getSlot()) {
				case HEAD -> e.add(GolemTypes.GOLEM_REGEN, new AttributeModifier(
						id, 1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(type.getSlot()));
				case CHEST -> e.add(GolemTypes.DYNAMIC_REDUCTION, new AttributeModifier(
						id, 1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(type.getSlot()));
				case LEGS -> e.add(Attributes.MOVEMENT_SPEED, new AttributeModifier(
						id, 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.bySlot(type.getSlot()));
			}
		});
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, level, list, flag);
		//TODO
		/*
		switch (getSlot()) {
			case HEAD -> list.add(MGLangData.IGNIS_BOOST_FIREBALL
					.get(Math.round(MGConfig.COMMON.fireballArmorBonus.get() * 100) + "%")
					.withStyle(ChatFormatting.GOLD));
			case CHEST -> list.add(MGLangData.IGNIS_BOOST_SOUL.get()
					.withStyle(ChatFormatting.GOLD));
			case LEGS -> list.add(MGLangData.IGNIS_BOOST_STRIKE
					.get(Math.round(MGConfig.COMMON.flameStrikeArmorBonus.get() * 100) + "%")
					.withStyle(ChatFormatting.GOLD));
		}

		 */
	}

	@Override
	public boolean emissive(LivingEntity user, ItemStack stack) {
		long prev = GolemItems.DC_TIMESTAMP.getOrDefault(stack, 0L);
		long time = user.level().getGameTime();
		return getSlot() == EquipmentSlot.CHEST && prev <= time;
	}

	@Override
	protected String namespace(String def) {
		return CataDispatch.MODID;
	}

}
