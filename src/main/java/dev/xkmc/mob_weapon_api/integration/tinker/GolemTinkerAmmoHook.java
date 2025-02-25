package dev.xkmc.mob_weapon_api.integration.tinker;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.ranged.BowAmmoModifierHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.Iterator;
import java.util.function.Predicate;

public class GolemTinkerAmmoHook {

	static boolean hasAmmo(IToolStackView tool, ItemStack bowStack, LivingEntity e, Predicate<ItemStack> predicate) {
		if (e.getProjectile(bowStack).isEmpty()) {
			Iterator<ModifierEntry> var4 = tool.getModifierList().iterator();

			ModifierEntry entry;
			do {
				if (!var4.hasNext()) {
					return false;
				}

				entry = var4.next();
			} while (entry.getHook(ModifierHooks.BOW_AMMO).findAmmo(tool, entry, e, ItemStack.EMPTY, predicate).isEmpty());

		}
		return true;
	}

	private static ItemStack findMatchingAmmo(ItemStack bow, LivingEntity living, Predicate<ItemStack> predicate) {
		InteractionHand[] var3 = InteractionHand.values();
		int var4 = var3.length;

		int i;
		for (i = 0; i < var4; ++i) {
			InteractionHand hand = var3[i];
			ItemStack stack = living.getItemInHand(hand);
			if (stack != bow && predicate.test(stack)) {
				return ForgeHooks.getProjectile(living, bow, stack);
			}
		}
		if (living instanceof Player player) {
			Inventory inventory = player.getInventory();

			for (i = 0; i < inventory.getContainerSize(); ++i) {
				ItemStack stack = inventory.getItem(i);
				if (!stack.isEmpty() && predicate.test(stack)) {
					return ForgeHooks.getProjectile(player, bow, stack);
				}
			}
		}
		return ForgeHooks.getProjectile(living, bow, ItemStack.EMPTY);
	}

	static ItemStack findAmmo(IToolStackView tool, ItemStack bow, LivingEntity e, Predicate<ItemStack> predicate) {
		int projectilesDesired = 1 + 2 * tool.getModifierLevel(TinkerModifiers.multishot.getId());
		Level level = e.level();
		boolean creative = e instanceof Player pl && pl.getAbilities().instabuild || level.isClientSide;
		ItemStack standardAmmo = e.getProjectile(bow);
		ItemStack resultStack = ItemStack.EMPTY;

		for (ModifierEntry entry : tool.getModifierList()) {
			BowAmmoModifierHook hook = entry.getHook(ModifierHooks.BOW_AMMO);
			ItemStack ammo = hook.findAmmo(tool, entry, e, standardAmmo, predicate);
			if (!ammo.isEmpty()) {
				if (creative) {
					return ItemHandlerHelper.copyStackWithSize(ammo, projectilesDesired);
				}

				resultStack = ItemHandlerHelper.copyStackWithSize(ammo, Math.min(projectilesDesired, ammo.getCount()));
				hook.shrinkAmmo(tool, entry, e, ammo, resultStack.getCount());
				break;
			}
		}

		if (resultStack.isEmpty()) {
			if (standardAmmo.isEmpty()) {
				return ItemStack.EMPTY;
			}

			if (creative) {
				return ItemHandlerHelper.copyStackWithSize(standardAmmo, projectilesDesired);
			}

			resultStack = standardAmmo.split(projectilesDesired);
		}

		if (resultStack.getCount() < projectilesDesired && !level.isClientSide) {
			ItemStack match = resultStack;
			predicate = (stack) -> {
				return ItemStack.isSameItemSameTags(stack, match);
			};

			do {
				if (standardAmmo.isEmpty()) {
					standardAmmo = findMatchingAmmo(bow, e, predicate);
				}

				Iterator var15 = tool.getModifierList().iterator();

				while (true) {
					if (var15.hasNext()) {
						ModifierEntry entry = (ModifierEntry) var15.next();
						BowAmmoModifierHook hook = entry.getHook(ModifierHooks.BOW_AMMO);
						ItemStack ammo = hook.findAmmo(tool, entry, e, standardAmmo, predicate);
						if (ammo.isEmpty()) {
							continue;
						}

						hook.shrinkAmmo(tool, entry, e, ammo, Math.min(projectilesDesired - resultStack.getCount(), ammo.getCount()));
						break;
					}

					if (standardAmmo.isEmpty()) {
						return resultStack;
					}

					int needed = projectilesDesired - resultStack.getCount();
					if (needed > standardAmmo.getCount()) {
						standardAmmo.shrink(needed);
						resultStack.grow(needed);
						return resultStack;
					}

					resultStack.grow(standardAmmo.getCount());
					standardAmmo.setCount(0);
					break;
				}
			} while (resultStack.getCount() < projectilesDesired);

			return resultStack;
		} else {
			return resultStack;
		}
	}

}
