package dev.xkmc.modulargolems.compat.materials.tinker.behavior;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.ranged.GolemShooterHelper;
import dev.xkmc.modulargolems.content.entity.humanoid.weapon.IBowBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
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
	public float powerForTime(int i) {
		return 1;
	}

	@Override
	public int pullTime(HumanoidGolemEntity golem, ItemStack stack) {
		return (int) Math.ceil(20 / ConditionalStatModifierHook.getModifiedStat(ToolStack.from(stack), golem, ToolStats.DRAW_SPEED));
	}

	@Override
	public boolean hasProjectile(HumanoidGolemEntity golem, ItemStack stack) {
		if (!(stack.getItem() instanceof ModifiableBowItem bow)) return false;
		ToolStack tool = ToolStack.from(stack);
		if (tool.isBroken()) return false;
		return GolemTinkerAmmoHook.hasAmmo(tool, stack, golem, bow.getSupportedHeldProjectiles());
	}

	public void performRangedAttack(HumanoidGolemEntity golem, LivingEntity target, float dist, ItemStack stack, InteractionHand hand) {
		if (!(stack.getItem() instanceof ModifiableBowItem bow)) return;
		shoot(bow, stack, golem.level(), golem, target);
	}

	public void shoot(ModifiableBowItem bowItem, ItemStack bowStack, Level level, HumanoidGolemEntity user, LivingEntity target) {
		ToolStack tool = ToolStack.from(bowStack);
		if (!hasProjectile(user, bowStack)) return;
		float velocity = ConditionalStatModifierHook.getModifiedStat(tool, user, ToolStats.VELOCITY);
		if (level.isClientSide) return;
		ItemStack ammo = GolemTinkerAmmoHook.findAmmo(tool, bowStack, user, bowItem.getSupportedHeldProjectiles());
		if (ammo.isEmpty()) {
			ammo = new ItemStack(Items.ARROW);
		}
		ArrowItem arrowItem = ammo.getItem() instanceof ArrowItem item ? item : (ArrowItem) Items.ARROW;
		float startAngle = ModifiableLauncherItem.getAngleStart(ammo.getCount());
		int primaryIndex = ammo.getCount() / 2;
		float inaccuracy = ModifierUtil.getInaccuracy(tool, user);

		var origin = user.getEyePosition().add(0, -0.1, 0);
		var consumer = GolemShooterHelper.getShootVector(target, origin, 3 * velocity, 0.05);

		for (int i = 0; i < ammo.getCount(); ++i) {
			AbstractArrow arrow = arrowItem.createArrow(level, ammo, user);
			float angle = startAngle + (float) (10 * i);
			shootArrow(consumer, arrow, angle, inaccuracy);
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

	private void shootArrow(GolemShooterHelper.ArrowConsumer cons, AbstractArrow arrow, float offset, float ina) {
		cons.rotate(offset).apply(arrow, ina);
		arrow.getPersistentData().putInt("DespawnFactor", 20);
	}

}
