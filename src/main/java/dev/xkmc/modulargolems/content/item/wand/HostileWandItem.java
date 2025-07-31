package dev.xkmc.modulargolems.content.item.wand;

import dev.xkmc.l2library.util.raytrace.IGlowingTarget;
import dev.xkmc.l2library.util.raytrace.RayTraceUtil;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.hostile.HostileGolemRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class HostileWandItem extends Item implements GolemInteractItem, IGlowingTarget {

	private static final int RANGE = 64;

	public HostileWandItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		if (level.isClientSide() && selected && entity instanceof Player player) {
			RayTraceUtil.clientUpdateTarget(player, RANGE);
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide()) {
			LivingEntity target = RayTraceUtil.serverGetTarget(player);
			if (target != null) {
				interactLivingEntity(stack, player, target, hand);
			}
		}
		return InteractionResultHolder.success(stack);
	}

	@Override
	public int getDistance(ItemStack itemStack) {
		return RANGE;
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity target, InteractionHand hand) {
		if (target instanceof AbstractGolemEntity<?, ?> golem) {
			if (user instanceof ServerPlayer sp) {
				if (!golem.isHostile()) {
					if (golem.getOwner() == user || sp.getAbilities().instabuild) {
						golem.setOwnerUUID(HostileGolemRegistry.DEFAULT.uuid);
					}
				} else if (sp.getAbilities().instabuild) {
					golem.setOwnerUUID(sp.getUUID());
				}
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

}
