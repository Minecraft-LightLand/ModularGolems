package dev.xkmc.modulargolems.content.item.wand;

import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.mode.GolemMode;
import dev.xkmc.modulargolems.content.entity.common.mode.GolemModes;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.menu.EquipmentsMenuPvd;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CommandWandItem extends Item implements WandItem {

	public CommandWandItem(Properties props) {
		super(props);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity target, InteractionHand hand) {
		if (!(target instanceof AbstractGolemEntity<?, ?> golem)) return InteractionResult.PASS;
		return command(target.level(), user, Wrappers.cast(golem)) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
	}

	private static boolean command(Level level, Player user, AbstractGolemEntity<?, ?> golem) {
		if (!golem.isAlliedTo(user)) return false;
		if (level.isClientSide()) return true;
		if (user.isShiftKeyDown()) {
			if (golem instanceof HumanoidGolemEntity e) {
				new EquipmentsMenuPvd(e).open((ServerPlayer) user);
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
		var list = target.level().getEntities(EntityTypeTest.forClass(AbstractGolemEntity.class), attacker.getBoundingBox().inflate(20), e -> true);
		for (var e : list) {
			if (e.getOwner() == attacker) {
				e.setTarget(target);
			}
		}
		return super.hurtEnemy(stack, target, attacker);
	}

	@Override
	public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> list, TooltipFlag pIsAdvanced) {
		list.add(MGLangData.WAND_COMMAND.get());
	}


}
