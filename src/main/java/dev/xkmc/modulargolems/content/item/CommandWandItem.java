package dev.xkmc.modulargolems.content.item;

import dev.xkmc.l2library.util.code.Wrappers;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CommandWandItem extends Item {

	public CommandWandItem(Properties props) {
		super(props);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity target, InteractionHand hand) {
		if (!(target instanceof AbstractGolemEntity<?, ?> golem)) return InteractionResult.PASS;
		return command(target.getLevel(), user, Wrappers.cast(golem)) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
	}

	private static boolean command(Level level, Player user, AbstractGolemEntity<?, ?> golem) {
		if (!golem.isAlliedTo(user)) return false;
		if (level.isClientSide()) return true;
		if (golem.getMode() == 1) {
			golem.setMode(0, BlockPos.ZERO);
		} else {
			golem.setMode(1, golem.blockPosition());
		}
		return true;
	}

}
