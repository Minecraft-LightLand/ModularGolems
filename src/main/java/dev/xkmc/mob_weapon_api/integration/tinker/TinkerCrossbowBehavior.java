package dev.xkmc.mob_weapon_api.integration.tinker;

import dev.xkmc.mob_weapon_api.api.projectile.CrossbowUseContext;
import dev.xkmc.mob_weapon_api.api.projectile.ICrossbowBehavior;
import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableCrossbowItem;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;

public class TinkerCrossbowBehavior implements ICrossbowBehavior {

	@Override
	public int chargeTime(LivingEntity golem, ItemStack stack) {
		return (int) Math.ceil(20 / ConditionalStatModifierHook.getModifiedStat(ToolStack.from(stack), golem, ToolStats.DRAW_SPEED));
	}

	@Override
	public void release(ItemStack stack) {

	}

	// from ModifiableCrossbowItem.releaseUsing
	@Override
	public boolean tryCharge(LivingEntity golem, ItemStack stack) {
		if (!(stack.getItem() instanceof ModifiableCrossbowItem bow)) return false;
		ToolStack tool = ToolStack.from(stack);
		if (tool.isBroken()) return false;
		ToolDataNBT data = tool.getPersistentData();
		ItemStack ammo = GolemTinkerAmmoHook.findAmmo(tool, stack, golem, bow.getSupportedHeldProjectiles());
		if (!ammo.isEmpty()) {
			golem.level().playSound(null, golem.getX(), golem.getY(), golem.getZ(), SoundEvents.CROSSBOW_LOADING_END, SoundSource.PLAYERS,
					1.0F, 1.0F / (golem.level().getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F);
			CompoundTag ammoNBT = ammo.save(new CompoundTag());
			data.put(ModifiableCrossbowItem.KEY_CROSSBOW_AMMO, ammoNBT);
			return true;
		}
		return false;
	}

	// from ModifiableCrossbowItem.fireCrossbow
	@Override
	public void performRangedAttack(CrossbowUseContext strategy, ItemStack stack, InteractionHand hand) {
		if (!(stack.getItem() instanceof ModifiableCrossbowItem)) return;
		LivingEntity golem = strategy.user();
		ToolStack tool = ToolStack.from(stack);
		if (tool.isBroken()) return;
		ToolDataNBT data = tool.getPersistentData();
		int damage = 0;
		ItemStack ammo = ItemStack.of(data.getCompound(ModifiableCrossbowItem.KEY_CROSSBOW_AMMO));
		float velocity = ConditionalStatModifierHook.getModifiedStat(tool, golem, ToolStats.VELOCITY) * strategy.getCrossbowVelocity(List.of(ammo));
		float inaccuracy = ModifierUtil.getInaccuracy(tool, golem);
		float startAngle = ModifiableCrossbowItem.getAngleStart(ammo.getCount());
		int primaryIndex = ammo.getCount() / 2;

		var pos = golem.getEyePosition().add(0, ammo.is(Items.FIREWORK_ROCKET) ? -0.15 : -0.1, 0);
		var cons = strategy.aim(pos, velocity, ammo.is(Items.FIREWORK_ROCKET) ? 0 : 0.05f, inaccuracy);

		for (int i = 0; i < ammo.getCount(); ++i) {
			AbstractArrow arrow = null;
			Projectile projectile;
			float angle;
			if (ammo.is(Items.FIREWORK_ROCKET)) {
				projectile = new FireworkRocketEntity(golem.level(), ammo, golem, pos.x, pos.y, pos.z, true);
				damage += 3;
			} else {
				arrow = strategy.createArrow(ammo, velocity);
				projectile = arrow;
				arrow.setCritArrow(true);
				arrow.setSoundEvent(SoundEvents.CROSSBOW_HIT);
				arrow.setShotFromCrossbow(true);
				++damage;
				angle = (float) (arrow.getBaseDamage() - 2.0 + tool.getStats().get(ToolStats.PROJECTILE_DAMAGE));
				arrow.setBaseDamage(ConditionalStatModifierHook.getModifiedStat(tool, golem, ToolStats.PROJECTILE_DAMAGE, angle));
			}

			angle = startAngle + (float) (10 * i);
			cons.shoot(projectile, angle);

			ModifierNBT modifiers = tool.getModifiers();
			projectile.getCapability(EntityModifierCapability.CAPABILITY).ifPresent((cap) -> cap.setModifiers(modifiers));
			ModDataNBT projectileData = PersistentDataCapability.getOrWarn(projectile);
			for (ModifierEntry entry : modifiers.getModifiers()) {
				entry.getHook(ModifierHooks.PROJECTILE_LAUNCH).onProjectileLaunch(tool, entry, golem, projectile, arrow, projectileData, i == primaryIndex);
			}
			golem.level().addFreshEntity(projectile);
			golem.level().playSound(null, golem.getX(), golem.getY(), golem.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, getRandomShotPitch(angle, golem.getRandom()));
		}
		tool.getPersistentData().remove(ModifiableCrossbowItem.KEY_CROSSBOW_AMMO);
		if (!strategy.bypassAllConsumption())
			ToolDamageUtil.damageAnimated(tool, damage, golem, hand);
	}

	@Override
	public boolean hasProjectile(ProjectileWeaponUser mob, ItemStack stack) {
		if (!(stack.getItem() instanceof ModifiableCrossbowItem bow)) return false;
		ToolStack tool = ToolStack.from(stack);
		if (tool.isBroken()) return false;
		return GolemTinkerAmmoHook.hasAmmo(tool, stack, mob.user(), bow.getSupportedHeldProjectiles());
	}

	@Override
	public boolean hasLoadedProjectile(ItemStack stack) {
		if (!(stack.getItem() instanceof ModifiableCrossbowItem)) return false;
		ToolStack tool = ToolStack.from(stack);
		if (tool.isBroken()) return false;
		ToolDataNBT data = tool.getPersistentData();
		return data.contains(ModifiableCrossbowItem.KEY_CROSSBOW_AMMO, 10);
	}

	@Override
	public List<ItemStack> getLoadedProjectile(ItemStack stack) {
		if (!(stack.getItem() instanceof ModifiableCrossbowItem)) return List.of();
		ToolStack tool = ToolStack.from(stack);
		if (tool.isBroken()) return List.of();
		ToolDataNBT data = tool.getPersistentData();
		if (!data.contains(ModifiableCrossbowItem.KEY_CROSSBOW_AMMO, 10)) return List.of();
		return List.of(ItemStack.of(data.getCompound(ModifiableCrossbowItem.KEY_CROSSBOW_AMMO)));
	}

	private static float getRandomShotPitch(float angle, RandomSource pRandom) {
		return angle == 0.0F ? 1.0F : 1.0F / (pRandom.nextFloat() * 0.5F + 1.8F) + 0.53F + angle / 10.0F;
	}

}
