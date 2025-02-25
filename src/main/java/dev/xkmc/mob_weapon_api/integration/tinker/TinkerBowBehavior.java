package dev.xkmc.mob_weapon_api.integration.tinker;

import dev.xkmc.mob_weapon_api.api.projectile.BowUseContext;
import dev.xkmc.mob_weapon_api.api.projectile.IBowBehavior;
import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableBowItem;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableLauncherItem;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class TinkerBowBehavior implements IBowBehavior {

	@Override
	public float getPowerForTime(BowUseContext user, ItemStack stack, int pullTime) {
		return 1;
	}

	@Override
	public int getStandardPullTime(BowUseContext user, ItemStack stack) {
		return (int) Math.ceil(20 / ConditionalStatModifierHook.getModifiedStat(ToolStack.from(stack), user.user(), ToolStats.DRAW_SPEED));
	}

	@Override
	public boolean hasProjectile(ProjectileWeaponUser user, ItemStack stack) {
		if (!(stack.getItem() instanceof ModifiableBowItem bow)) return false;
		ToolStack tool = ToolStack.from(stack);
		if (tool.isBroken()) return false;
		return GolemTinkerAmmoHook.hasAmmo(tool, stack, user.user(), bow.getSupportedHeldProjectiles());
	}

	public void shootArrow(BowUseContext user, float dist, ItemStack stack, InteractionHand hand) {
		if (!(stack.getItem() instanceof ModifiableBowItem bow)) return;
		shoot(bow, stack, user);
	}

	// from ModifiableBowItem.releaseUsing
	public void shoot(ModifiableBowItem bowItem, ItemStack bowStack, BowUseContext strategy) {
		var user = strategy.user();
		var level = user.level();
		ToolStack tool = ToolStack.from(bowStack);
		if (!hasProjectile(strategy, bowStack)) return;
		float velocity = strategy.getInitialVelocityFactor() * ConditionalStatModifierHook.getModifiedStat(tool, user, ToolStats.VELOCITY);
		if (level.isClientSide) return;
		ItemStack ammo = GolemTinkerAmmoHook.findAmmo(tool, bowStack, user, bowItem.getSupportedHeldProjectiles());
		if (ammo.isEmpty()) {
			ammo = new ItemStack(Items.ARROW);
		}
		ArrowItem arrowItem = ammo.getItem() instanceof ArrowItem item ? item : (ArrowItem) Items.ARROW;
		float startAngle = ModifiableLauncherItem.getAngleStart(ammo.getCount());
		int primaryIndex = ammo.getCount() / 2;
		float inaccuracy = ModifierUtil.getInaccuracy(tool, user) * strategy.getInitialInaccuracy();

		// custom shoot direction
		var origin = user.getEyePosition().add(0, -0.1, 0);
		var consumer = strategy.aim(origin, velocity, 0.05f, inaccuracy);

		for (int i = 0; i < ammo.getCount(); ++i) {
			AbstractArrow arrow = arrowItem.createArrow(level, ammo, user);
			float angle = startAngle + (float) (10 * i);
			consumer.shoot(arrow, angle);
			arrow.setCritArrow(true);
			float baseDmg = (float) (arrow.getBaseDamage() - 2.0 + tool.getStats().get(ToolStats.PROJECTILE_DAMAGE));
			arrow.setBaseDamage(ConditionalStatModifierHook.getModifiedStat(tool, user, ToolStats.PROJECTILE_DAMAGE, baseDmg));
			ModifierNBT modifiers = tool.getModifiers();
			arrow.getCapability(EntityModifierCapability.CAPABILITY).ifPresent((cap) -> cap.setModifiers(modifiers));
			ModDataNBT arrowData = PersistentDataCapability.getOrWarn(arrow);

			for (ModifierEntry entry : modifiers.getModifiers()) {
				entry.getHook(ModifierHooks.PROJECTILE_LAUNCH).onProjectileLaunch(tool, entry, user, arrow, arrow, arrowData, i == primaryIndex);
			}
			level.addFreshEntity(arrow);
			level.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F + angle / 10.0F);
		}
		ToolDamageUtil.damageAnimated(tool, ammo.getCount(), user, user.getUsedItemHand());
	}

}
