package dev.xkmc.modulargolems.content.item.wand;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.l2library.content.raytrace.RayTraceUtil;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.init.advancement.GolemTriggers;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class DispenseWand extends BaseWandItem implements GolemInteractItem {

	private static void iter(Player player, Predicate<ItemStack> use) {
		if (use.test(player.getOffhandItem())) return;
		for (int i = 0; i < 36; i++) {
			if (use.test(player.getInventory().getItem(i)))
				return;
		}
	}

	public DispenseWand(Properties properties, @Nullable ItemEntry<? extends BaseWandItem> base) {
		super(properties, MGLangData.WAND_SUMMON_RIGHT, MGLangData.WAND_SUMMON_SHIFT, base);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
		ItemStack stack = user.getItemInHand(hand);
		if (!level.isClientSide()) {
			boolean all = user.isShiftKeyDown();
			Vec3 pos = user.position();
			if (!all) {
				var result = RayTraceUtil.rayTraceBlock(level, user, MGConfig.COMMON.summonDistance.get());
				if (result.getType() == HitResult.Type.BLOCK) {
					pos = result.getLocation();
				}
			}
			Vec3 finalPos = pos;
			int[] counter = new int[]{0};
			iter(user, golem -> {
				if (golem.getItem() instanceof GolemHolder<?, ?> holder) {
					if (holder.summon(golem, level, finalPos, user, null)) {
						counter[0]++;
						return !all;
					}
				}
				return false;
			});
			if (counter[0] > 1 && user instanceof ServerPlayer sp) {
				GolemTriggers.MAS_SUMMON.trigger(sp, counter[0]);
			}
		}
		return InteractionResultHolder.success(stack);
	}

}
