package dev.xkmc.modulargolems.content.item.wand;

import dev.xkmc.l2library.util.raytrace.IGlowingTarget;
import dev.xkmc.l2library.util.raytrace.RayTraceUtil;
import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.mode.GolemMode;
import dev.xkmc.modulargolems.content.entity.common.mode.GolemModes;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.menu.EquipmentsMenuPvd;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CommandWandItem extends Item implements WandItem, IGlowingTarget {

	private static final int RANGE = 32;

	public CommandWandItem(Properties props) {
		super(props);
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
		if (!(target instanceof AbstractGolemEntity<?, ?> golem)) {
			if (!user.level().isClientSide()) {
				hurtEnemy(stack, target, user);
			}
			return InteractionResult.SUCCESS;
		}
		return command(target.level(), user, Wrappers.cast(golem)) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
	}

	private static boolean command(Level level, Player user, AbstractGolemEntity<?, ?> golem) {
		if (!golem.isAlliedTo(user)) return false;
		if (level.isClientSide()) return true;
		if (user.isShiftKeyDown()) {
			if (golem instanceof HumanoidGolemEntity || golem instanceof MetalGolemEntity) {
				new EquipmentsMenuPvd(golem).open((ServerPlayer) user);
				return true;
			}
			return false;
		}
		GolemMode mode = GolemModes.nextMode(golem.getMode());
		golem.setMode(mode.getID(), mode.hasPos() ? golem.blockPosition() : BlockPos.ZERO);
		return true;
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (attacker instanceof ServerPlayer pl) {
			pl.sendSystemMessage(MGLangData.CALL_ATTACK.get(target.getDisplayName()), true);
		}
		var list = target.level().getEntities(EntityTypeTest.forClass(AbstractGolemEntity.class), attacker.getBoundingBox().inflate(20), e -> true);
		for (var e : list) {
			if (e.getOwner() == attacker) {
				e.setTarget(target);
			}
		}
		return false;
	}

	@Override
	public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> list, TooltipFlag pIsAdvanced) {
		list.add(MGLangData.WAND_COMMAND.get());
	}


}
