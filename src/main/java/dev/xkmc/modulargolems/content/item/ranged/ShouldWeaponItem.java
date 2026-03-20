package dev.xkmc.modulargolems.content.item.ranged;

import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public abstract class ShouldWeaponItem extends Item implements IShoulderWeapon {

	public ShouldWeaponItem(Properties properties) {
		super(properties.stacksTo(1));
	}

	@Override
	public ResourceLocation getModelTexture(MetalGolemEntity entity, ItemStack stack, InteractionHand hand) {
		var id = BuiltInRegistries.ITEM.getKey(this);
		return id.withPath(e -> "textures/equipments/" + e + ".png");
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> list, TooltipFlag flag) {
		list.add(MGLangData.GOLEM_EQUIPMENT.get(GolemTypes.ENTITY_GOLEM.get().getDescription().copy().withStyle(ChatFormatting.GOLD))
				.withStyle(ChatFormatting.UNDERLINE));
		list.add(MGLangData.SHOULDER_WEAPON.get());
	}

}
