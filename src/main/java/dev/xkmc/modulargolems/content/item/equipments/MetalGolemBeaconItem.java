package dev.xkmc.modulargolems.content.item.equipments;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.function.Consumer;

public class MetalGolemBeaconItem extends MetalGolemArmorItem implements TickEquipmentItem {

	private static Component effDesc(MobEffect eff, int amp) {
		MutableComponent lang = Component.translatable(eff.getDescriptionId());
		if (amp > 0) {
			lang = Component.translatable("potion.withAmplifier", lang,
					Component.translatable("potion.potency." + amp));
		}
		return lang.withStyle(eff.getCategory().getTooltipFormatting());
	}

	public MetalGolemBeaconItem(Properties properties, int def, int tough, Identifier model) {
		super(properties, ArmorType.BOOTS, def, tough, model, e ->
				e.add(Attributes.MOVEMENT_SPEED, new AttributeModifier(
						ModularGolems.loc(EquipmentSlot.FEET.getName() + "_armor"), -0.5f,
						AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.FEET)
		);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext level, TooltipDisplay disp, Consumer<Component> list, TooltipFlag flag) {
		list.accept(MGLangData.BEACON_BOOTS.get(
				effDesc(MobEffects.STRENGTH.value(), 1),
				effDesc(MobEffects.RESISTANCE.value(), 1)
		));
		super.appendHoverText(stack, level, disp, list, flag);
	}

	@Override
	public void tick(ItemStack stack, Level level, Entity user) {
		if (level.getGameTime() % 80D != 0)
			return;
		if (user instanceof AbstractGolemEntity<?, ?> golem) {
			double range = 40;
			AABB aabb = golem.getBoundingBox().inflate(range).expandTowards(0D, level.getHeight(), 0D);
			for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, aabb, golem::isAlliedTo)) {
				e.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 200, 1, true, true), golem);
				e.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 200, 1, true, true), golem);
				e.heal(2);
			}
		}
	}

}
