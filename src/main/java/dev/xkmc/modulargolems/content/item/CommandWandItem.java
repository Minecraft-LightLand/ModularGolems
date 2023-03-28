package dev.xkmc.modulargolems.content.item;

import dev.xkmc.l2library.util.code.Wrappers;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.mode.GolemMode;
import dev.xkmc.modulargolems.content.entity.common.mode.GolemModes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CommandWandItem extends Item implements WandItem {

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
		GolemMode mode = GolemModes.nextMode(golem.getMode());
		golem.setMode(mode.getID(), mode.hasPos() ? golem.blockPosition() : BlockPos.ZERO);
		return true;
	}

}
