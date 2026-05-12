package dev.xkmc.modulargolems.content.item.wand;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.l2core.content.raytrace.RayTraceUtil;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.init.advancement.GolemTriggers;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class DispenseWand extends BaseWandItem implements GolemInteractItem {

	public DispenseWand(Properties properties, @Nullable ItemEntry<? extends BaseWandItem> base) {
		super(properties, MGLangData.WAND_SUMMON_RIGHT, MGLangData.WAND_SUMMON_SHIFT, base);
	}

	@Override
	public InteractionResult use(Level level, Player user, InteractionHand hand) {
		ItemStack stack = user.getItemInHand(hand);
		if (user instanceof ServerPlayer sp) {
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
			GolemTransportHandler.summonGolemFromPlayer(sp, golem -> {
				if (golem.getItem() instanceof GolemHolder<?, ?> holder) {
					if (holder.summon(golem, level, finalPos, user, null)) {
						counter[0]++;
						return !all;
					}
				}
				return false;
			});
			if (counter[0] > 1) {
				GolemTriggers.MAS_SUMMON.get().trigger(sp, counter[0]);
			}
		}
		return InteractionResult.SUCCESS;
	}

}
