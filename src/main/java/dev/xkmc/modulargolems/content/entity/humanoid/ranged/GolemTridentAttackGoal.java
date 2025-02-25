package dev.xkmc.modulargolems.content.entity.humanoid.ranged;

import dev.xkmc.modulargolems.content.entity.goals.GolemMeleeGoal;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.weapon.IRangedWeaponGoal;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;

public class GolemTridentAttackGoal extends RangedAttackGoal implements IRangedWeaponGoal {
	private final HumanoidGolemEntity golem;
	private final GolemMeleeGoal melee;

	public GolemTridentAttackGoal(HumanoidGolemEntity pRangedAttackMob, double pSpeedModifier, int pAttackInterval, float pAttackRadius, GolemMeleeGoal melee) {
		super(pRangedAttackMob, pSpeedModifier, pAttackInterval, pAttackRadius);
		this.golem = pRangedAttackMob;
		this.melee = melee;
	}

	public boolean canUse() {
		LivingEntity target = golem.getTarget();
		if (target == null || !super.canUse()) return false;
		if (melee.canReachTarget(target)) return false;
		InteractionHand hand = golem.getWeaponHand();
		return GolemShooterHelper.isValidThrowableWeapon(this.golem, this.golem.getItemInHand(hand), hand);
	}

	public void start() {
		super.start();
		golem.setAggressive(true);
		golem.setInRangeAttack(true);
		golem.startUsingItem(golem.getWeaponHand());
	}

	public void stop() {
		super.stop();
		golem.stopUsingItem();
		golem.setAggressive(false);
		golem.setInRangeAttack(false);
	}

	@Override
	public void performRangedAttack(HumanoidGolemEntity golem, LivingEntity target, float power, ItemStack stack, InteractionHand hand) {
		var throwable = GolemShooterHelper.throwWeapon(golem, stack, hand);
		if (throwable.isThrowable()) {
			Projectile projectile = throwable.createProjectile(golem.level());
			GolemShooterHelper.shootAimHelper(target, projectile);
			golem.playSound(SoundEvents.TRIDENT_THROW, 1.0F, 1.0F / (golem.getRandom().nextFloat() * 0.4F + 0.8F));
			projectile.getPersistentData().putInt("DespawnFactor", 20);
			golem.level().addFreshEntity(projectile);
			stack.hurtAndBreak(1, golem, e -> e.broadcastBreakEvent(InteractionHand.MAIN_HAND));
		}
	}

}