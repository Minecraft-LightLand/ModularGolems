package dev.xkmc.modulargolems.content.item;

import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RiderWandItem extends Item implements WandItem {
	public RiderWandItem(Properties props) {
		super(props);
	}

	public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity target, InteractionHand hand) {
		if (!(target instanceof AbstractGolemEntity<?, ?> golem)) return InteractionResult.PASS;
		return ride(target.level(), user, Wrappers.cast(golem)) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
	}

	private static boolean ride(Level level, Player user, AbstractGolemEntity<?, ?> golem) {
		if (!golem.isAlliedTo(user)) return false;
		if (level.isClientSide()) return true;
		if (golem instanceof DogGolemEntity e) {
			if(e.isInSittingPose()){
				return false;
			}
			user.startRiding(e, true);
			return true;
		}
		return true;


	}

	public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> list, TooltipFlag pIsAdvanced) {
		list.add(MGLangData.WAND_RIDER.get());
	}

}