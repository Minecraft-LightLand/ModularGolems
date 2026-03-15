package dev.xkmc.modulargolems.content.item.ranged;

import com.google.common.collect.ImmutableMultimap;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class MetalGolemMechaBowItem extends MetalGolemBowItem implements IMultiShotBow {

	public MetalGolemMechaBowItem(Properties properties, int baseline, Consumer<ImmutableMultimap.Builder<Attribute, AttributeModifier>> attr) {
		super(properties, baseline, attr);
	}

	public MetalGolemMechaBowItem(Properties properties, int baseline, int atk, Consumer<ImmutableMultimap.Builder<Attribute, AttributeModifier>> attr) {
		super(properties, baseline, atk, attr);
	}

	public MetalGolemMechaBowItem(Properties properties, int baseline, int atk) {
		super(properties, baseline, atk);
	}

	@Override
	public int getMaxShoot(@Nullable LivingEntity user, ItemStack stack) {
		return 3 + stack.getEnchantmentLevel(Enchantments.MULTISHOT);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, level, list, flag);
		list.add(MGLangData.MULTI_SHOT.get(getMaxShoot(null, stack)).withStyle(ChatFormatting.GRAY));
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return super.canApplyAtEnchantingTable(stack, enchantment) || enchantment == Enchantments.MULTISHOT;
	}
}
