package dev.xkmc.modulargolems.content.item.ranged;

import dev.xkmc.l2core.init.reg.ench.EnchHelper;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class MetalGolemMechaBowItem extends MetalGolemBowItem implements IMultiShotBow {

	public MetalGolemMechaBowItem(Properties properties, int baseline, Consumer<ItemAttributeModifiers.Builder> attr) {
		super(properties, baseline, attr);
	}

	public MetalGolemMechaBowItem(Properties properties, int baseline, int atk, Consumer<ItemAttributeModifiers.Builder> attr) {
		super(properties, baseline, atk, attr);
	}

	public MetalGolemMechaBowItem(Properties properties, int baseline, int atk) {
		super(properties, baseline, atk);
	}

	@Override
	public int getMaxShoot(@Nullable LivingEntity user, ItemStack stack) {
		return 3 + EnchHelper.getLv(stack, Enchantments.MULTISHOT) * 2;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext level, TooltipDisplay disp, Consumer<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, level, disp, list, flag);
		list.accept(MGLangData.MULTI_SHOT.get(getMaxShoot(null, stack)).withStyle(ChatFormatting.GRAY));
	}

	@Override
	public boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment) {
		return super.isPrimaryItemFor(stack, enchantment) || enchantment.is(Enchantments.MULTISHOT);
	}

	@Override
	public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
		return super.supportsEnchantment(stack, enchantment) || enchantment.is(Enchantments.MULTISHOT);
	}

}
