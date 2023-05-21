package dev.xkmc.modulargolems.content.item;

import dev.xkmc.l2library.util.raytrace.RayTraceUtil;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

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



}
