package dev.xkmc.modulargolems.content.item.wand;

import dev.xkmc.l2core.content.raytrace.IGlowingTarget;
import dev.xkmc.l2core.content.raytrace.RayTraceUtil;
import dev.xkmc.modulargolems.content.capability.GolemTracker;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.hostile.HostileGolemRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class HostileWandItem extends Item implements GolemInteractItem, IGlowingTarget {

	private static final int RANGE = 64;

	public HostileWandItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot selected) {
		if (level.isClientSide() && selected == EquipmentSlot.MAINHAND && entity instanceof Player player) {
			RayTraceUtil.clientUpdateTarget(player, RANGE);
		}
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide()) {
			LivingEntity target = RayTraceUtil.serverGetTarget(player);
			if (target != null) {
				interactLivingEntity(stack, player, target, hand);
			}
		}
		return InteractionResult.SUCCESS;
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
						golem.untrack(GolemTracker.Status.OTHER_RETRIEVED, null);
						golem.setOwnerUUID(HostileGolemRegistry.DEFAULT.uuid);
					}
				} else if (sp.getAbilities().instabuild) {
					golem.setOwnerUUID(sp.getUUID());
				}
			}
		} else {
			target.addTag("HostileGolemTarget");
		}
		return InteractionResult.SUCCESS;
	}

}
