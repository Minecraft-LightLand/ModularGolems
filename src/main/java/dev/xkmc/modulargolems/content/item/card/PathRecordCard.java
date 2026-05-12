package dev.xkmc.modulargolems.content.item.card;

import dev.xkmc.modulargolems.content.client.outline.BlockOutliner;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class PathRecordCard extends Item {

	@Nullable
	public static Pos getList(ItemStack stack) {
		return GolemItems.DC_PATH.get(stack);
	}

	public static void addPos(ItemStack stack, Level level, BlockPos pos) {
		var id = level.dimension().identifier();
		Pos old = getList(stack);
		if (old != null && old.level.equals(id)) {
			old = old.copy();
			old.pos.add(pos);
			GolemItems.DC_PATH.set(stack, old);
		} else {
			GolemItems.DC_PATH.set(stack, new Pos(id, new ArrayList<>(List.of(pos))));
		}
	}

	public static boolean togglePos(ItemStack stack, Level level, BlockPos pos) {
		var id = level.dimension().identifier();
		var ans = getList(stack);
		if (ans == null || !ans.level().equals(id) || !ans.pos().contains(pos)) {
			addPos(stack, level, pos);
			return true;
		}
		ans = ans.copy();
		ans.pos().remove(pos);
		if (ans.pos.isEmpty())
			stack.remove(GolemItems.DC_PATH);
		else GolemItems.DC_PATH.set(stack, ans);
		return false;
	}

	public PathRecordCard(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		ItemStack stack = ctx.getItemInHand();
		Level level = ctx.getLevel();
		if (!level.isClientSide()) {
			Player player = ctx.getPlayer();
			if (player != null && player.isShiftKeyDown()) {
				stack.remove(GolemItems.DC_PATH);
				player.sendSystemMessage(MGLangData.PATH_CLEAR.get());
				return InteractionResult.SUCCESS;
			}
			BlockPos pos = ctx.getClickedPos();
			BlockState state = level.getBlockState(pos);
			if (!state.getShape(level, pos).isEmpty()) {
				pos = pos.relative(ctx.getClickedFace());
			}
			var list = getList(stack);
			if (list != null && !list.pos.isEmpty()) {
				var last = list.pos.getLast();
				if (!list.match(level)) {
					if (player != null) {
						player.sendSystemMessage(MGLangData.PATH_ERR_DIM.get());
					}
					return InteractionResult.FAIL;
				}
				if (!list.pos().contains(pos) && pos.distSqr(last) > 256) {
					if (player != null) {
						player.sendSystemMessage(MGLangData.PATH_ERR_DIST.get());
					}
					return InteractionResult.FAIL;
				}
			}
			if (togglePos(stack, level, pos)) {
				if (player != null) {
					player.sendSystemMessage(MGLangData.PATH_ADD.get());
				}
			} else {
				if (player != null) {
					player.sendSystemMessage(MGLangData.PATH_REMOVE.get());
				}
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
		if (slot==EquipmentSlot.MAINHAND && owner instanceof Player player && level.isClientSide()) {
			var pos = getList(itemStack);
			if (pos != null && pos.level().equals(level.dimension().identifier())) {
				BlockOutliner.drawOutlines(player, pos.pos);
			}
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext level, TooltipDisplay display, Consumer<Component> list, TooltipFlag flag) {
		int size = Optional.ofNullable(getList(stack)).map(e -> e.pos().size()).orElse(0);
		list.accept(MGLangData.PATH_COUNT.get(Component.literal("" + size)
				.withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY));
		list.accept(MGLangData.PATH.get().withStyle(ChatFormatting.GRAY));
	}

	public record Pos(Identifier level, ArrayList<BlockPos> pos) {

		public Pos copy() {
			return new Pos(level, new ArrayList<>(pos));
		}

		public boolean match(Level level) {
			return level().equals(level.dimension().identifier());
		}

	}

}
