package dev.xkmc.modulargolems.content.item.wand;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.l2core.content.raytrace.IGlowingTarget;
import dev.xkmc.l2core.content.raytrace.RayTraceUtil;
import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.compat.curio.CurioCompatRegistry;
import dev.xkmc.modulargolems.content.client.outline.BlockOutliner;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.mode.GolemMode;
import dev.xkmc.modulargolems.content.entity.mode.GolemModes;
import dev.xkmc.modulargolems.content.item.card.ConfigCard;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsMenuPvd;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.jetbrains.annotations.Nullable;

public class CommandWandItem extends BaseWandItem implements GolemInteractItem, IGlowingTarget {

	private static final int RANGE = 64;

	public CommandWandItem(Properties properties, @Nullable ItemEntry<? extends BaseWandItem> base) {
		super(properties, MGLangData.WAND_COMMAND_RIGHT, MGLangData.WAND_COMMAND_SHIFT, base);
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
		if (level.isClientSide() && slot == EquipmentSlot.MAINHAND && entity instanceof Player player) {
			RayTraceUtil.clientUpdateTarget(player, RANGE);
			if (RayTraceUtil.serverGetTarget(player) instanceof AbstractGolemEntity<?, ?> golem) {
				if (golem.getMode() == GolemModes.ROUTE) {
					BlockOutliner.drawOutlines(player, golem.getPatrolList());
				}
			}
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
		if (!(target instanceof AbstractGolemEntity<?, ?> golem)) {
			if (user instanceof ServerPlayer sp) {
				if (target instanceof OwnableEntity ownable && ownable.getOwner() == user) {
					if (MGConfig.COMMON.allowEditCuriosForOthers.get())
						CurioCompatRegistry.tryOpen(sp, target);
				} else {
					hurtEnemy(stack, target, user);
				}
			}
			return InteractionResult.SUCCESS;
		}
		return command(target.level(), user, Wrappers.cast(golem)) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
	}

	private static boolean command(Level level, Player user, AbstractGolemEntity<?, ?> golem) {
		if (!ConfigCard.getFilter(user).test(golem)) return false;
		if (!golem.canWandModify(user)) return false;
		if (level.isClientSide()) return true;
		if (user.isShiftKeyDown()) {
			new EquipmentsMenuPvd(golem).open((ServerPlayer) user);
			return true;
		}
		GolemMode mode = GolemModes.nextMode(golem.getMode());
		golem.setMode(mode.getID(), mode.hasPos() ? golem.blockPosition() : BlockPos.ZERO);
		return true;
	}

	@Override
	public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (!(attacker instanceof Player player)) return;
		var list = target.level().getEntities(EntityTypeTest.forClass(AbstractGolemEntity.class), attacker.getBoundingBox().inflate(32), e -> true);
		int size = 0;
		for (var e : list) {
			if (!ConfigCard.getFilter(player).test(e)) return;
			if (!e.canWandModify(player)) return;
			if (e.getOwner() == attacker) {
				size++;
				e.resetTarget(target);
			}
		}
		if (attacker instanceof ServerPlayer pl) {
			pl.sendSystemMessage(MGLangData.CALL_ATTACK.get(size, target.getDisplayName()), true);
		}
	}

}
