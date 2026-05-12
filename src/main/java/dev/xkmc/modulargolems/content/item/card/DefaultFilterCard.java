package dev.xkmc.modulargolems.content.item.card;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class DefaultFilterCard extends TargetFilterCard {

	public static boolean defaultPredicate(LivingEntity e) {
		return e instanceof Enemy && !(e instanceof Creeper) || e instanceof AbstractGolemEntity<?, ?> golem && golem.isHostile();
	}

	public DefaultFilterCard(Properties properties) {
		super(properties);
	}

	@Override
	public Predicate<LivingEntity> mayTarget(ItemStack stack) {
		return DefaultFilterCard::defaultPredicate;
	}

	@Override
	protected InteractionResult removeLast(Player player, ItemStack stack) {
		return InteractionResult.PASS;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext level, TooltipDisplay display, Consumer<Component> list, TooltipFlag flag) {
		list.accept(MGLangData.TARGET_DEFAULT.get());
	}

}
