package dev.xkmc.modulargolems.compat.materials.tinker.behavior;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.ranged.GolemShooterHelper;
import dev.xkmc.modulargolems.content.entity.humanoid.ranged.ICrossbowBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
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
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableCrossbowItem;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class TinkerCrossbowBehavior implements ICrossbowBehavior {

	@Override
	public int chargeTime(HumanoidGolemEntity golem, ItemStack stack) {
		return (int) Math.ceil(20 / ConditionalStatModifierHook.getModifiedStat(ToolStack.from(stack), golem, ToolStats.DRAW_SPEED));
	}

	@Override
	public void release(ItemStack stack) {

	}

	@Override
	public boolean tryCharge(HumanoidGolemEntity golem, ItemStack stack) {
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

	@Override
	public void performRangedAttack(HumanoidGolemEntity golem, LivingEntity target, float dist, ItemStack stack, InteractionHand hand) {
		if (!(stack.getItem() instanceof ModifiableCrossbowItem)) return;
		ToolStack tool = ToolStack.from(stack);
		if (tool.isBroken()) return;
		ToolDataNBT data = tool.getPersistentData();
		CompoundTag heldAmmo = data.getCompound(ModifiableCrossbowItem.KEY_CROSSBOW_AMMO);


		int damage = 0;
		float velocity = ConditionalStatModifierHook.getModifiedStat(tool, golem, ToolStats.VELOCITY);
		float inaccuracy = ModifierUtil.getInaccuracy(tool, golem);
		ItemStack ammo = ItemStack.of(heldAmmo);
		float startAngle = ModifiableCrossbowItem.getAngleStart(ammo.getCount());
		int primaryIndex = ammo.getCount() / 2;

		var pos = golem.getEyePosition().add(0, ammo.is(Items.FIREWORK_ROCKET) ? -0.15 : -0.1, 0);
		var cons = GolemShooterHelper.getShootVector(target, pos, velocity * 3, 0.05);

		for (int i = 0; i < ammo.getCount(); ++i) {
			AbstractArrow arrow = null;
			Projectile projectile;
			float speed;
			float angle;
			if (ammo.is(Items.FIREWORK_ROCKET)) {
				projectile = new FireworkRocketEntity(golem.level(), ammo, golem, golem.getX(), golem.getEyeY() - 0.15, golem.getZ(), true);
				speed = 1.5F;
				damage += 3;
			} else {
				ArrowItem arrowItem = ammo.getItem() instanceof ArrowItem item ? item : (ArrowItem) Items.ARROW;
				arrow = arrowItem.createArrow(golem.level(), ammo, golem);
				projectile = arrow;
				arrow.setCritArrow(true);
				arrow.setSoundEvent(SoundEvents.CROSSBOW_HIT);
				arrow.setShotFromCrossbow(true);
				speed = 3.0F;
				++damage;
				angle = (float) (arrow.getBaseDamage() - 2.0 + tool.getStats().get(ToolStats.PROJECTILE_DAMAGE));
				arrow.setBaseDamage(ConditionalStatModifierHook.getModifiedStat(tool, golem, ToolStats.PROJECTILE_DAMAGE, angle));
			}

			angle = startAngle + (float) (10 * i);
			cons.rotate(angle).apply(projectile, inaccuracy);

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
		ToolDamageUtil.damageAnimated(tool, damage, golem, hand);
	}

	@Override
	public boolean hasProjectile(HumanoidGolemEntity mob, ItemStack stack) {
		if (!(stack.getItem() instanceof ModifiableCrossbowItem bow)) return false;
		ToolStack tool = ToolStack.from(stack);
		if (tool.isBroken()) return false;
		return GolemTinkerAmmoHook.hasAmmo(tool, stack, mob, bow.getSupportedHeldProjectiles());
	}

	@Override
	public boolean hasLoadedProjectile(ItemStack stack) {
		if (!(stack.getItem() instanceof ModifiableCrossbowItem)) return false;
		ToolStack tool = ToolStack.from(stack);
		if (tool.isBroken()) return false;
		ToolDataNBT data = tool.getPersistentData();
		return data.contains(ModifiableCrossbowItem.KEY_CROSSBOW_AMMO, 10);
	}

	private static float getRandomShotPitch(float angle, RandomSource pRandom) {
		return angle == 0.0F ? 1.0F : 1.0F / (pRandom.nextFloat() * 0.5F + 1.8F) + 0.53F + angle / 10.0F;
	}

}
