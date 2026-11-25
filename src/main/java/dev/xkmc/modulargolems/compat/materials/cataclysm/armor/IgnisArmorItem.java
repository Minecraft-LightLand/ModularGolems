package dev.xkmc.modulargolems.compat.materials.cataclysm.armor;

import dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers.IgnisJumpModifier;
import dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers.IgnisFireballModifier;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataCompatRegistry;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataDispatch;
import dev.xkmc.modulargolems.content.entity.common.GolemFormAppearance;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemArmorItem;
import net.minecraft.core.registries.BuiltInRegistries;
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
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;

public class IgnisArmorItem extends MetalGolemArmorItem {

	public IgnisArmorItem(Properties properties, ArmorItem.Type type, int defense, float toughness, ResourceLocation model) {
		this(properties, type, defense, toughness, 0, model);
	}

	public IgnisArmorItem(Properties properties, ArmorItem.Type type, int defense, float toughness, float knockbackResistance, ResourceLocation model) {
		super(properties, type, defense, toughness, model, e -> {
			var id = ModularGolems.loc("ignis_armor_" + type.getName());
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
		switch (getSlot()) {
			case HEAD -> list.add(MGLangData.IGNIS_FIREBALL_BOOST
					.get(Math.round(MGConfig.COMMON.fireballArmorBonus.get() * 100) + "%",
							CataArmorHelper.skillName(CataCompatRegistry.IGNIS_FIREBALL.get()),
							Math.round(IgnisFireballModifier.EXPLOSION_RADIUS_BONUS * 100) + "%")
					.withStyle(ChatFormatting.GOLD));
			case CHEST -> {
				// %s 不带颜色，跟随整行统一的 GOLD
				list.add(MGLangData.SKILL_IGNORE_HEALTH.get(CataArmorHelper.materialName("ignitium"))
						.withStyle(ChatFormatting.GOLD));
			}
			case LEGS -> list.add(MGLangData.IGNIS_CHARGE_BOOST
					.get(Math.round(MGConfig.COMMON.flameStrikeArmorBonus.get() * 100) + "%",
							CataArmorHelper.skillName(CataCompatRegistry.IGNIS_JUMP.get()),
							CataArmorHelper.skillName(CataCompatRegistry.IGNIS_CHARGE.get()),
							Math.round(IgnisJumpModifier.FLAME_STRIKE_DURATION_BONUS * 100) + "%")
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

	@Override
	public ResourceLocation getModelTexture(LivingEntity user) {
		ResourceLocation rl = BuiltInRegistries.ITEM.getKey(this);
		assert rl != null;
		var str = "textures/equipments/" + rl.getPath();
		if (GolemFormAppearance.visualFinal(user, CataDispatch.ignisBlue(user), CataArmorHelper.TWO_THIRDS))
			str += "_soul";
		return ResourceLocation.fromNamespaceAndPath(namespace(rl.getNamespace()), str + ".png");
	}

	@Override
	public ResourceLocation getEmissiveModelTexture(LivingEntity user) {
		ResourceLocation rl = BuiltInRegistries.ITEM.getKey(this);
		assert rl != null;
		var str = "textures/equipments/" + rl.getPath();
		if (GolemFormAppearance.visualFinal(user, CataDispatch.ignisBlue(user), CataArmorHelper.TWO_THIRDS))
			str += "_soul";
		return ResourceLocation.fromNamespaceAndPath(namespace(rl.getNamespace()), str + "_emissive.png");
	}

}
