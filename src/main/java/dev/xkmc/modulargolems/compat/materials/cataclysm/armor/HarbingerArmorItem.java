package dev.xkmc.modulargolems.compat.materials.cataclysm.armor;

import com.google.common.collect.ImmutableMultimap;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataDispatch;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemArmorItem;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class HarbingerArmorItem extends MetalGolemArmorItem {

	public HarbingerArmorItem(Properties properties, ArmorItem.Type type, int defense, float toughness, ResourceLocation model) {
		super(properties, type, defense, toughness, model);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, level, list, flag);
		switch (getSlot()) {
			case HEAD -> list.add(MGLangData.HARBINGER_BOOST_LASER
					.get(Math.round(MGConfig.COMMON.laserArmorBonus.get() * 100) + "%")
					.withStyle(ChatFormatting.GOLD));
			case CHEST -> list.add(MGLangData.HARBINGER_BOOST_MISSILE
					.get(Math.round(MGConfig.COMMON.missileArmorBonus.get() * 100) + "%")
					.withStyle(ChatFormatting.GOLD));
		}
	}

	@Override
	public boolean emissive() {
		return true;
	}

	@Override
	protected String namespace(String def) {
		return CataDispatch.MODID;
	}

	@Override
	protected void additionalAttributes(ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder) {
		super.additionalAttributes(builder);
		var id = ModularGolems.loc("harbinger_armor");
		builder.put(L2DamageTracker.ABSORB, new AttributeModifier(id, 1, AttributeModifier.Operation.ADD_VALUE));
		switch (getSlot()) {
			case HEAD -> builder.put(GolemTypes.GOLEM_REGEN, new AttributeModifier(
					id, 1, AttributeModifier.Operation.ADD_VALUE));
			case CHEST -> builder.put(GolemTypes.GOLEM_SWEEP, new AttributeModifier(
					id, 1, AttributeModifier.Operation.ADD_VALUE));
			case LEGS -> builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(
					id, 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
		}
	}

}
