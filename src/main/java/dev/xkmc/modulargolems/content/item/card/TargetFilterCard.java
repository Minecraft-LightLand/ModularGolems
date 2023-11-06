package dev.xkmc.modulargolems.content.item.card;

import dev.xkmc.modulargolems.content.item.wand.GolemInteractItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public abstract class TargetFilterCard extends Item implements GolemInteractItem {

	public TargetFilterCard(Properties properties) {
		super(properties);
	}

	public abstract Predicate<LivingEntity> mayTarget(ItemStack stack);

	protected abstract InteractionResultHolder<ItemStack> removeLast(Player player, ItemStack stack);

	protected InteractionResultHolder<ItemStack> onUse(Player player, ItemStack stack) {
		return InteractionResultHolder.pass(stack);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (player.isShiftKeyDown()) {
			return removeLast(player, stack);
		} else {
			return onUse(player, stack);
		}
	}

}
