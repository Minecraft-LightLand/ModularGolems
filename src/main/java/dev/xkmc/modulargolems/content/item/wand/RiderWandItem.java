package dev.xkmc.modulargolems.content.item.wand;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class RiderWandItem extends BaseWandItem implements GolemInteractItem {

	public RiderWandItem(Properties properties, @Nullable ItemEntry<? extends BaseWandItem> base) {
		super(properties, MGLangData.WAND_RIDER, null, base);
	}

	public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity target, InteractionHand hand) {
		if (!(target instanceof AbstractGolemEntity<?, ?> golem)) return InteractionResult.PASS;
		return ride(target.level(), user, Wrappers.cast(golem)) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
	}

	private static boolean ride(Level level, Player user, AbstractGolemEntity<?, ?> golem) {
		if (!ConfigCard.getFilter(user).test(golem)) return false;
		if (!golem.canModify(user) && !(golem.getControllingPassenger() instanceof Player)) return false;
		if (level.isClientSide()) return true;
		if (golem instanceof DogGolemEntity e) {
			user.startRiding(e, false);
			return true;
		}
		return true;
	}


}