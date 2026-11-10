package dev.xkmc.modulargolems.content.item.wand;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.l2library.util.raytrace.IGlowingTarget;
import dev.xkmc.l2library.util.raytrace.RayTraceUtil;
import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import dev.xkmc.modulargolems.content.item.card.ConfigCard;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class RiderWandItem extends BaseWandItem implements GolemInteractItem, IGlowingTarget {

	private static final int RANGE = 64;

	public RiderWandItem(Properties properties, @Nullable ItemEntry<? extends BaseWandItem> base) {
		super(properties, MGLangData.WAND_RIDER, null, base);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		if (level.isClientSide() && selected && entity instanceof Player player) {
			RayTraceUtil.clientUpdateTarget(player, RANGE);
		}
	}

	@Override
	public int getDistance(ItemStack itemStack) {
		return RANGE;
	}

	public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity target, InteractionHand hand) {
		if (user.getVehicle() instanceof AbstractGolemEntity<?, ?>) {
			if (!user.level().isClientSide())
				user.stopRiding();
			return InteractionResult.SUCCESS;
		}
		if (!(target instanceof AbstractGolemEntity<?, ?> golem)) return InteractionResult.PASS;
		return ride(target.level(), user, Wrappers.cast(golem)) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
		ItemStack stack = user.getItemInHand(hand);
		if (user.getVehicle() instanceof AbstractGolemEntity<?, ?>) {
			if (!level.isClientSide())
				user.stopRiding();
			return InteractionResultHolder.success(stack);
		}
		return InteractionResultHolder.pass(stack);
	}

	/**
	 * Riding normally needs the golem itself to be clicked, which the key cannot do, so the key resolves the
	 * golem the player is aiming at instead. Dismounting while mounted behaves as it does on right click.
	 * The golem still has to be within the player's entity reach, otherwise the key would ride from across
	 * the map compared to right clicking it.
	 */
	@Override
	public void useFromKey(Level level, Player user, InteractionHand hand) {
		if (user.getVehicle() instanceof AbstractGolemEntity<?, ?>) {
			if (!level.isClientSide()) user.stopRiding();
			return;
		}
		if (RayTraceUtil.serverGetTarget(user) instanceof AbstractGolemEntity<?, ?> golem) {
			if (!user.canReach(golem, 0)) return;
			ride(level, user, Wrappers.cast(golem));
		}
	}

	private static boolean ride(Level level, Player user, AbstractGolemEntity<?, ?> golem) {
		if (!ConfigCard.getFilter(user).test(golem)) return false;
		if (!golem.canWandModify(user) && !(golem.getControllingPassenger() instanceof Player)) return false;
		if (level.isClientSide()) return true;
		if (golem instanceof DogGolemEntity e) {
			// a shoulder mounted weapon occupies the back, so the dog cannot be ridden
			if (!e.getShoulderSlot().getItem().isEmpty()) return false;
			user.startRiding(e, false);
			return true;
		}
		return true;
	}


}
