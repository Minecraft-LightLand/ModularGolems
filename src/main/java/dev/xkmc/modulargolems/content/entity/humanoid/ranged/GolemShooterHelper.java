package dev.xkmc.modulargolems.content.entity.humanoid.ranged;

import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;

import java.util.HashSet;

public class GolemShooterHelper {

	private static final HashSet<Class<?>> BLACKLIST = new HashSet<>();

	public static boolean isValidThrowableWeapon(Item stack) {
		return stack instanceof TridentItem;
	}

	@SuppressWarnings("ConstantConditions")
	public static boolean arrowIsInfinite(ItemStack arrow, ItemStack bow) {
		if (!(arrow.getItem() instanceof ArrowItem item)) {
			return false;
		}
		if (BLACKLIST.contains(item.getClass())) {
			return false;
		}
		try {
			return item.isInfinite(arrow, bow, null);
		} catch (NullPointerException npe) {
			BLACKLIST.add(item.getClass());
		}
		return false;
	}

	public void shootAimHelper(AbstractArrow arrow) {

	}

}
