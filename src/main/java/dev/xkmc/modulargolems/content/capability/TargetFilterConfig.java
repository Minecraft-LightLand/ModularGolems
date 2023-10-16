package dev.xkmc.modulargolems.content.capability;

import com.mojang.datafixers.util.Either;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.modulargolems.content.item.card.NameFilterCard;
import dev.xkmc.modulargolems.content.item.card.TargetFilterCard;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@SerialClass
public class TargetFilterConfig {

	public static final int LINE = 18;

	@SerialClass.SerialField
	protected final ArrayList<ItemStack> hostileTo = new ArrayList<>();

	@SerialClass.SerialField
	protected final ArrayList<ItemStack> friendlyTo = new ArrayList<>();

	public boolean internalMatch(ArrayList<ItemStack> list, ItemStack stack) {
		for (ItemStack filter : list) {
			if (stack.getItem() == filter.getItem()) {
				if (ItemStack.isSameItemSameTags(stack, filter)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean aggressiveToward(LivingEntity le) {
		for (var e : hostileTo) {
			if (e.getItem() instanceof TargetFilterCard card) {
				if (card.mayTarget(e).test(le)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean friendlyToward(LivingEntity le) {
		for (var e : friendlyTo) {
			if (e.getItem() instanceof TargetFilterCard card) {
				if (card.mayTarget(e).test(le)) {
					return true;
				}
			}
		}
		return false;
	}

	public void initDefault() {
		ItemStack friendly = GolemItems.CARD_NAME.asStack();
		NameFilterCard.setList(friendly, List.of(Either.right(MGTagGen.GOLEM_FRIENDLY)));
		friendlyTo.add(friendly);
	}

}
