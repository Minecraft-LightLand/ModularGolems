package dev.xkmc.modulargolems.compat.materials.goety.title;

import dev.xkmc.mob_weapon_api.api.ai.ISmartUser;
import dev.xkmc.mob_weapon_api.api.ai.IWeaponHolder;
import dev.xkmc.mob_weapon_api.api.goals.IMeleeGoal;
import dev.xkmc.mob_weapon_api.api.projectile.IBowBehavior;
import dev.xkmc.mob_weapon_api.example.goal.SmartBowAttackGoal;
import dev.xkmc.mob_weapon_api.registry.WeaponRegistry;
import dev.xkmc.modulargolems.compat.materials.goety.GoetyCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.goety.revelation.GRCompatRegistry;
import dev.xkmc.modulargolems.content.entity.common.SweepGolemEntity;
import dev.xkmc.modulargolems.content.entity.targeting.TargetManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.entity.EntityTypeTest;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Optional;

public class ApollyonBowGoal<E extends SweepGolemEntity<?, ?>> extends SmartBowAttackGoal<E> {

	public ApollyonBowGoal(E mob, IMeleeGoal melee, double speed, double radius) {
		super(mob, melee, speed, radius);
	}

	public ApollyonBowGoal(E mob, IWeaponHolder holder, IMeleeGoal melee, double speed, double radius) {
		super(mob, holder, melee, speed, radius);
	}

	protected void makeTarget(int range, int max) {
		var list = mob.level().getEntities(
				EntityTypeTest.forClass(LivingEntity.class),
				mob.getBoundingBox().inflate(range),
				e -> mob.canAttack(e) && mob.hasLineOfSight(e));
		list.sort(Comparator.comparing(ke -> Optional.ofNullable(
				TargetManager.predicateTarget(mob, ke)
		).map(Enum::ordinal).orElse(100)));

		targets = new LinkedList<>();
		for (var e : list) {
			if (targets.size() < max)
				targets.add(e);
			else break;
		}
	}

	@Override
	public void tick() {
		this.doMelee();
		this.strafing();
		LivingEntity target = this.mob.getTarget();
		InteractionHand hand = this.holder.getWeaponHand();
		ItemStack stack = this.mob.getItemInHand(hand);
		Optional<IBowBehavior> weapon = WeaponRegistry.BOW.get(this.mob, stack);
		if (weapon.isEmpty()) return;
		if (this.mob.isUsingItem() && target != null) {
			ISmartUser user = this.holder.toUser();
			double dist = this.mob.distanceTo(target);
			if (this.seeTime < -60) {
				this.mob.stopUsingItem();
			} else if (this.seeTime > 0) {
				int i = this.mob.getTicksUsingItem();
				int pullTime = weapon.get().getPreferredPullTime(user, stack, dist);
				if (i >= pullTime) {
					if (performRangedAttack(weapon.get().getPowerForTime(user, stack, i), stack, hand))
						mob.stopUsingItem();
				} else {
					weapon.get().tickUsingBow(user, stack);
				}
			}
		} else if (this.seeTime >= -60) {
			ISmartUser user = this.holder.toUser();
			if (target != null) {
				double dist = this.mob.distanceTo(target);
				int pullTime = weapon.get().getPreferredPullTime(user, stack, dist);
				if (pullTime <= 0) {
					this.performRangedAttack(target, 0.0F, stack, hand);
					return;
				}
			}
			this.mob.startUsingItem(hand);
			weapon.ifPresent((e) -> e.startUsingBow(user, stack));
		}
	}

	@Nullable
	private LinkedList<LivingEntity> targets = null;
	private int count;

	public boolean performRangedAttack(float power, ItemStack stack, InteractionHand hand) {
		int lv = mob.getModifiers().getOrDefault(GRCompatRegistry.BOW.get(), 0);
		int range = 35;
		int max = 4 + 4 * lv;
		int maxCount = 2 + lv;
		if (targets == null) {
			makeTarget(range, max);
			count = stack.is(GoetyCompatRegistry.REV_BOW) ? maxCount : 1;
		}
		if (targets.isEmpty()) {
			targets = null;
			return true;
		}
		var old = mob.getTarget();
		for (var e : targets) {
			if (e.isAlive()) {
				mob.setTarget(e);
				performRangedAttack(e, power, stack, hand);
			}
		}
		mob.setTarget(old);
		count--;
		if (count <= 0) {
			targets = null;
			return true;
		}
		return false;
	}
}
