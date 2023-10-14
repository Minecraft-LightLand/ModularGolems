package dev.xkmc.modulargolems.content.item.card;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

public abstract class ClickEntityFilterCard<T> extends TargetFilterCard {

	public ClickEntityFilterCard(Properties properties) {
		super(properties);
	}

	protected abstract List<T> getList(ItemStack stack);

	protected abstract T getValue(LivingEntity entity);

	protected abstract void setList(ItemStack stack, List<T> list);

	protected InteractionResult addTargetEntity(ItemStack stack, LivingEntity target) {
		var list = getList(stack);
		if (list.contains(getValue(target))) {
			return InteractionResult.SUCCESS;
		}
		list.add(getValue(target));
		setList(stack, list);
		return InteractionResult.SUCCESS;
	}

	protected InteractionResult removeTargetEntity(ItemStack stack, LivingEntity target) {
		var list = getList(stack);
		if (list.contains(getValue(target))) {
			list.remove(getValue(target));
			setList(stack, list);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.FAIL;
	}

	@Override
	protected InteractionResultHolder<ItemStack> removeLast(ItemStack stack) {
		var list = getList(stack);
		if (list.size() == 0) return InteractionResultHolder.fail(stack);
		list.remove(list.size() - 1);
		setList(stack, list);
		return InteractionResultHolder.success(stack);
	}


	@Override
	public Predicate<LivingEntity> mayTarget(ItemStack stack) {
		var set = new HashSet<>(getList(stack));
		return e -> set.contains(getValue(e));
	}


	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
		ItemStack item = player.getItemInHand(hand);
		if (player.isShiftKeyDown()) {
			return removeTargetEntity(item, target);
		} else {
			return addTargetEntity(item, target);
		}
	}

}
