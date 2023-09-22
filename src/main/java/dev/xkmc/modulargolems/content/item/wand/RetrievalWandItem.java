package dev.xkmc.modulargolems.content.item.wand;

import dev.xkmc.l2library.util.raytrace.RayTraceUtil;
import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RetrievalWandItem extends Item implements GolemInteractItem {

	public RetrievalWandItem(Properties props) {
		super(props);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
		ItemStack stack = user.getItemInHand(hand);
		if (user.isShiftKeyDown()) {
			var result = RayTraceUtil.rayTraceEntity(user, MGConfig.COMMON.retrieveDistance.get(), e -> (e instanceof AbstractGolemEntity<?, ?> golem) && golem.isAlliedTo(user));
			if (result == null) return InteractionResultHolder.fail(stack);
			var golem = result.getEntity();
			return attemptRetrieve(level, user, Wrappers.cast(golem)) ? InteractionResultHolder.success(stack) : InteractionResultHolder.fail(stack);
		} else {
			var list = level.getEntities(EntityTypeTest.forClass(AbstractGolemEntity.class), user.getBoundingBox().inflate(MGConfig.COMMON.retrieveRange.get()), e -> true);
			if (list.size() == 0) {
				return InteractionResultHolder.pass(stack);
			}
			boolean success = false;
			for (var golem : list) {
				success |= attemptRetrieve(level, user, golem);
			}
			return success ? InteractionResultHolder.success(stack) : InteractionResultHolder.fail(stack);
		}
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity target, InteractionHand hand) {
		if (!(target instanceof AbstractGolemEntity<?, ?> golem)) return InteractionResult.PASS;
		return attemptRetrieve(target.level(), user, Wrappers.cast(golem)) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
	}

	private static boolean attemptRetrieve(Level level, Player user, AbstractGolemEntity<?, ?> golem) {
		//TODO offhand card
		if (!golem.isAlliedTo(user)) return false;
		if (level.isClientSide()) return true;
		user.getInventory().placeItemBackInInventory(golem.toItem());
		return true;
	}

	@Override
	public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> list, TooltipFlag pIsAdvanced) {
		list.add(MGLangData.WAND_RETRIEVE.get());
	}


}
