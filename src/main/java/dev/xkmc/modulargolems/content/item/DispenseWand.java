package dev.xkmc.modulargolems.content.item;

import dev.xkmc.l2library.util.raytrace.RayTraceUtil;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.init.data.LangData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class DispenseWand extends Item implements WandItem {

	private static void iter(Player player, Predicate<ItemStack> use) {
		if (use.test(player.getOffhandItem())) return;
		for (int i = 0; i < 36; i++) {
			if (use.test(player.getInventory().getItem(i)))
				return;
		}
	}

	public DispenseWand(Properties props) {
		super(props);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
		ItemStack stack = user.getItemInHand(hand);
		if (!level.isClientSide()) {
			boolean all = user.isShiftKeyDown();
			Vec3 pos = user.position();
			if (!all) {
				var result = RayTraceUtil.rayTraceBlock(level, user, 64);
				if (result.getType() == HitResult.Type.BLOCK) {
					pos = result.getLocation();
				}
			}
			Vec3 finalPos = pos;
			iter(user, golem -> {
				if (golem.getItem() instanceof GolemHolder holder) {
					return holder.summon(golem, level, finalPos, user) && !all;
				}
				return false;
			});
		}
		return InteractionResultHolder.success(stack);
	}

	@Override
	public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> list, TooltipFlag pIsAdvanced) {
		list.add(LangData.WAND_SUMMON.get());
	}

}
